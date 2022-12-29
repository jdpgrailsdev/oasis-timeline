/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jdpgrailsdev.oasis.timeline;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.jdpgrailsdev.oasis.timeline.config.IntegrationTestConfiguration;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.data.model.mastodon.MastodonStatus;
import com.jdpgrailsdev.oasis.timeline.mocks.MockDateUtils;
import com.jdpgrailsdev.oasis.timeline.schedule.MastodonTimelineEventScheduler;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/** Integration test suite to validate interactions with the Mastodon API. */
@ActiveProfiles("test")
@SpringBootTest(classes = {IntegrationTestConfiguration.class})
@ContextConfiguration(initializers = {WireMockInitializer.class})
class EndToEndMastodonIntegrationTests {

  private static final String MASTODON_RESPONSE_FILE = "/mastodon_response.json";

  private static final String MASTODON_URI = "/api/v1/statuses";

  private static final String SIZE_ASSERTION_MESSAGE = "expected number of messages produced";

  private static final String STATUS_BODY_MESSAGE = "expected status message body";

  private static final String UPDATE_HASHTAGS = "\n\n#Oasis #OTD #TodayInMusic #britpop";

  @Autowired private MastodonTimelineEventScheduler scheduler;

  @Autowired private MockDateUtils dateUtils;

  @Autowired private WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    WireMock.configureFor("localhost", 9093);
  }

  @AfterEach
  void cleanup() {
    dateUtils.reset();
    wireMockServer.resetAll();
  }

  @Test
  @DisplayName(
      "test that when the scheduler is invoked for a date with timeline events, the events are"
          + " published to Mastodon")
  void testSchedulingUpdates() throws IOException, URISyntaxException {
    final String twitterResponse =
        new String(
            Files.readAllBytes(new ClassPathResource(MASTODON_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(MASTODON_URI)).willReturn(okJson(twitterResponse)));

    dateUtils.setToday("October 2");
    scheduler.publishTimelineEvent();

    verify(2, postRequestedFor(urlEqualTo(MASTODON_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    assertEquals(2, serveEventList.size(), SIZE_ASSERTION_MESSAGE);

    final String update1 =
        TimelineDataType.RELEASES.getEmoji()
            + " #OnThisDay in 1995, #Oasis release '(What's the Story) Morning Glory?', their "
            + "second studio album, on Creation Records.  The album would propel the band to a "
            + "worldwide fame, selling over 12 million copies around the world."
            + UPDATE_HASHTAGS;
    validateUpdate(update1, serveEventList.get(1).getRequest());

    final String update2 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 2016, '#Oasis: Supersonic' premieres at the Manchester Odeon "
            + "Printworks in Manchester, UK.  The event is attended by Liam Gallagher, Paul "
            + "\"Bonehead\" Arthurs and director Mat Whitecross.  Liam, Bonehead and Mat take "
            + "part in a Q&A with the audience after the screening of the film."
            + UPDATE_HASHTAGS;
    validateUpdate(update2, serveEventList.get(0).getRequest());
  }

  @Test
  @DisplayName(
      "test that when the scheduled task is invoked for each date on the calendar, the task is"
          + " executed")
  void testSchedulingSpecificDate() throws IOException {
    final String twitterResponse =
        new String(
            Files.readAllBytes(new ClassPathResource(MASTODON_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(MASTODON_URI)).willReturn(okJson(twitterResponse)));

    final LocalDate end = LocalDate.of(2021, 1, 1);
    for (LocalDate date = LocalDate.of(2020, 1, 1); date.isBefore(end); date = date.plusDays(1)) {
      final ZonedDateTime localDate = date.atStartOfDay(ZoneId.systemDefault());
      final String today =
          localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
              + " "
              + localDate.getDayOfMonth();

      dateUtils.setToday(today);
      scheduler.publishTimelineEvent();
    }

    verify(postRequestedFor(urlEqualTo(MASTODON_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    serveEventList.forEach(
        event -> {
          try {
            assertTrue(
                decodeUpdate(event.getRequest()).length() <= MastodonStatus.STATUS_LIMIT,
                SIZE_ASSERTION_MESSAGE);
          } catch (final URISyntaxException e) {
            fail(e);
          }
        });
  }

  @Test
  @DisplayName("test that hashtag replacement works for names that end with an apostrophe")
  void testHandleHashtagReplacement() throws IOException, URISyntaxException {
    final String twitterResponse =
        new String(
            Files.readAllBytes(new ClassPathResource(MASTODON_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(MASTODON_URI)).willReturn(okJson(twitterResponse)));

    dateUtils.setToday("May 7");
    scheduler.publishTimelineEvent();

    verify(2, postRequestedFor(urlEqualTo(MASTODON_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    assertEquals(2, serveEventList.size(), SIZE_ASSERTION_MESSAGE);

    final String update1 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1996, #Oasis appear on Chris Evans' Radio 1 Breakfast Show and "
            + "announce that they will be playing two nights at Knebworth in August."
            + UPDATE_HASHTAGS;
    validateUpdate(update1, serveEventList.get(0).getRequest());

    final String update2 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1994, Maggie Mouzakitis attends her first #Oasis gig as the "
            + "band's tour manager at their show at The Old Trout in Windsor, "
            + "Berkshire, UK."
            + UPDATE_HASHTAGS;
    validateUpdate(update2, serveEventList.get(1).getRequest());
  }

  private void validateUpdate(final String update, final LoggedRequest loggedRequest)
      throws URISyntaxException {
    final String request = decodeUpdate(loggedRequest);
    //    final GeoLocation location = decodeLocation(loggedRequest);
    assertEquals(update, request, STATUS_BODY_MESSAGE);
    //    assertEquals(Tweet.LOCATION, location, TWEET_LOCATION_MESSAGE);
  }

  private String decodeUpdate(final LoggedRequest request) throws URISyntaxException {
    final Map<String, Object> params = decodeRequest(request);
    return params.getOrDefault("status", null).toString();
  }

  //  private GeoLocation decodeLocation(final LoggedRequest request) throws URISyntaxException {
  //    final Map<String, Object> params = decodeRequest(request);
  //    final double latitude = Double.parseDouble(params.getOrDefault("lat", "0.0").toString());
  //    final double longitude = Double.parseDouble(params.getOrDefault("long", "0.0").toString());
  //    return GeoLocation.of(latitude, longitude);
  //  }

  private Map<String, Object> decodeRequest(final LoggedRequest request) throws URISyntaxException {
    final List<NameValuePair> params =
        URLEncodedUtils.parse(
            new URI(request.getUrl() + "?" + request.getBodyAsString()), StandardCharsets.UTF_8);
    return params.stream()
        .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
  }
}
