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
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.mocks.MockDateUtils;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
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
import twitter4j.GeoLocation;

@ActiveProfiles("test")
@SpringBootTest(classes = {IntegrationTestConfiguration.class})
@ContextConfiguration(initializers = {WireMockInitializer.class})
class EndToEndIntegrationTests {

  private static final String SIZE_ASSERTION_MESSAGE = "expected number of tweets produced";

  private static final String TWEET_BODY_MESSAGE = "expected tweet message body";

  private static final String TWEET_LOCATION_MESSAGE = "expected tweet location";

  private static final String TWITTER_RESPONSE_FILE = "/twitter_response.json";

  private static final String TWITTER_URI = "/statuses/update.json";

  @Autowired private TwitterTimelineEventScheduler scheduler;

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
          + " published to Twitter")
  void testSchedulingTweets() throws IOException, URISyntaxException {
    final String twitterResponse =
        new String(
            Files.readAllBytes(new ClassPathResource(TWITTER_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(TWITTER_URI)).willReturn(okJson(twitterResponse)));

    dateUtils.setToday("October 2");
    scheduler.publishTimelineTweet();

    verify(3, postRequestedFor(urlEqualTo(TWITTER_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    assertEquals(3, serveEventList.size(), SIZE_ASSERTION_MESSAGE);

    final String tweet1 =
        TimelineDataType.RELEASES.getEmoji()
            + " #OnThisDay in 1995, @Oasis release '(What's the Story) Morning Glory?', their "
            + "second studio album, on Creation Records.  The album would propel the band to a "
            + "worldwide fame, selling over 12 million copies around the world."
            + "\n\n@creationrecords #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet1, serveEventList.get(2).getRequest());

    final String tweet2 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 2016, '@Oasis: Supersonic' premieres at the Manchester Odeon "
            + "Printworks in Manchester, UK.  The event is attended by Liam Gallagher, Paul "
            + "\"Bonehead\" Arthurs and director Mat Whitecross.  Liam, Bonehead and Mat take "
            + "part in a Q&A with the audience after...";
    validateTweet(tweet2, serveEventList.get(1).getRequest());

    final String tweet3 =
        "... the screening of the film."
            + "\n\n@boneheadspage @liamGallagher @matwhitecross #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet3, serveEventList.get(0).getRequest());
  }

  @Test
  @DisplayName(
      "test that when the scheduler is invoked for a date with timeline events and the generated"
          + " tweet exceeds the size limit, the event is split and published to Twitter")
  void testSchedulingTweetsExceedingTheLimit() throws IOException, URISyntaxException {
    final String twitterResponse =
        new String(
            Files.readAllBytes(new ClassPathResource(TWITTER_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(TWITTER_URI)).willReturn(okJson(twitterResponse)));

    dateUtils.setToday("April 24");
    scheduler.publishTimelineTweet();

    verify(3, postRequestedFor(urlEqualTo(TWITTER_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    assertEquals(3, serveEventList.size(), SIZE_ASSERTION_MESSAGE);

    final String tweet1 =
        TimelineDataType.RELEASES.getEmoji()
            + " #OnThisDay in 1995, @Oasis release 'Some Might Say', the first single from "
            + "the forthcoming album '(What's The Story) Morning Glory?', on Creation "
            + "Records.  It would go on to become @Oasis' first number one single and is "
            + "the last recording to...";
    validateTweet(tweet1, serveEventList.get(2).getRequest());

    final String tweet2 =
        "... feature the original lineup.  The single "
            + "includes the b-sides 'Talk Tonight', 'Acquiesce' and 'Headshrinker'."
            + "\n\n@creationrecords #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet2, serveEventList.get(1).getRequest());

    final String tweet3 =
        TimelineDataType.VIDEOS.getEmoji()
            + " #OnThisDay in 1995, @Oasis release the music video for 'Some Might Say'.  "
            + "The video is directed by Stuart Fryer."
            + "\n\n#Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet3, serveEventList.get(0).getRequest());
  }

  @Test
  @DisplayName(
      "test that when the scheduler is invoked for a date with timeline events that should include"
          + " mentions, the events are published to Twitter with the mentions included")
  void testSchedulingTweetsWithMentions() throws IOException, URISyntaxException {
    final String twitterResponse =
        new String(
            Files.readAllBytes(new ClassPathResource(TWITTER_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(TWITTER_URI)).willReturn(okJson(twitterResponse)));

    dateUtils.setToday("August 18");
    scheduler.publishTimelineTweet();

    verify(8, postRequestedFor(urlEqualTo(TWITTER_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    assertEquals(8, serveEventList.size(), SIZE_ASSERTION_MESSAGE);

    final String tweet1 =
        TimelineDataType.GIGS.getEmoji()
            + " #OnThisDay in 1991, @Oasis perform their first gig under the name \"@Oasis\" "
            + "at The Boardwalk in Manchester, UK.  At this point, the band is a 4-piece made "
            + "up of Liam Gallagher, Paul \"Bonehead\" Arthurs, Paul \"Guigsy\" McGuigan and "
            + "Tony McCarroll.";
    validateTweet(tweet1, serveEventList.get(7).getRequest());

    final String tweet2 =
        "The Inspiral Carpets are in attendance, "
            + "accompanied by roadie Noel Gallagher, who sees his brother's band perform live "
            + "for the first time.\n\n@boneheadspage @liamGallagher @noelgallagher "
            + "@TonyMcCarrolls #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet2, serveEventList.get(6).getRequest());

    final String tweet3 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1994, Noel Gallagher, Liam Gallagher and Paul \"Bonehead\" "
            + "Arthurs appear on MTV's Most Wanted to promote the release of their upcoming "
            + "debut album 'Definitely Maybe' and a live show later that evening at the "
            + "Kentish Town Forum in...";
    validateTweet(tweet3, serveEventList.get(5).getRequest());

    final String tweet4 =
        "... London, UK.  After a short "
            + "interview with host Davina McCall, the trio perform 'Whatever' and 'Live "
            + "Forever'.  The performance is notable as Bonehead accompanies Noel and Liam on "
            + "the piano instead of his customary rhythm guitar.\n\n@boneheadspage...";
    validateTweet(tweet4, serveEventList.get(4).getRequest());

    final String tweet5 =
        "... @liamGallagher @noelgallagher " + "@ThisisDavina #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet5, serveEventList.get(3).getRequest());

    final String tweet6 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1996, Noel Gallagher joins Paul Weller and Jools Holland on "
            + "stage at the first V Festival in Hylands Park, Chelmsford, UK to play 'Eye of "
            + "the Storm'.  During the song, Noel asks the crowd to \"show your appreciation\" "
            + "and informs the...";
    validateTweet(tweet6, serveEventList.get(2).getRequest());

    final String tweet7 =
        "... crowd that \"Alan White's brother\" (Steve White) is on drums.  Paul Weller returns"
            + " the favor by thanking \"Mr. Liam Gallagher\" after the jam.  It would be another"
            + " nine years before @Oasis would finally appear at the festival in 2005.\n\n"
            + "@drummerwhitey...";
    validateTweet(tweet7, serveEventList.get(1).getRequest());

    final String tweet8 =
        "... @liamGallagher @noelgallagher @paulwellerHQ #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet8, serveEventList.get(0).getRequest());
  }

  @Test
  @DisplayName(
      "test that when the scheduled task is invoked for each date on the calendar, the task is"
          + " executed")
  void testSchedulingSpecificDate() throws IOException {
    final String twitterResponse =
        new String(
            Files.readAllBytes(new ClassPathResource(TWITTER_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(TWITTER_URI)).willReturn(okJson(twitterResponse)));

    final LocalDate end = LocalDate.of(2021, 1, 1);
    for (LocalDate date = LocalDate.of(2020, 1, 1); date.isBefore(end); date = date.plusDays(1)) {
      final ZonedDateTime localDate = date.atStartOfDay(ZoneId.systemDefault());
      final String today =
          localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
              + " "
              + localDate.getDayOfMonth();

      dateUtils.setToday(today);
      scheduler.publishTimelineTweet();
    }

    verify(postRequestedFor(urlEqualTo(TWITTER_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    serveEventList.forEach(
        event -> {
          try {
            assertTrue(
                decodeTweet(event.getRequest()).length() <= Tweet.TWEET_LIMIT,
                SIZE_ASSERTION_MESSAGE);
          } catch (final URISyntaxException e) {
            fail(e);
          }
        });
  }

  @Test
  @DisplayName("test that Twitter handle replacement works for names that end with an apostrophe")
  void testTwitterHandleReplacement() throws IOException, URISyntaxException {
    final String twitterResponse =
        new String(
            Files.readAllBytes(new ClassPathResource(TWITTER_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(TWITTER_URI)).willReturn(okJson(twitterResponse)));

    dateUtils.setToday("May 7");
    scheduler.publishTimelineTweet();

    verify(2, postRequestedFor(urlEqualTo(TWITTER_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    assertEquals(2, serveEventList.size(), SIZE_ASSERTION_MESSAGE);

    final String tweet1 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1996, @Oasis appear on Chris Evans' Radio 1 Breakfast Show and "
            + "announce that they will be playing two nights at Knebworth in August."
            + "\n\n@achrisevans #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet1, serveEventList.get(0).getRequest());

    final String tweet2 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1994, Maggie Mouzakitis attends her first @Oasis gig as the "
            + "band's tour manager at their show at The Old Trout in Windsor, "
            + "Berkshire, UK."
            + "\n\n#Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet2, serveEventList.get(1).getRequest());
  }

  private void validateTweet(final String tweet, final LoggedRequest loggedRequest)
      throws URISyntaxException {
    final String request = decodeTweet(loggedRequest);
    final GeoLocation location = decodeLocation(loggedRequest);
    assertEquals(tweet, request, TWEET_BODY_MESSAGE);
    assertEquals(Tweet.LOCATION, location, TWEET_LOCATION_MESSAGE);
  }

  private String decodeTweet(final LoggedRequest request) throws URISyntaxException {
    final Map<String, Object> params = decodeRequest(request);
    return params.getOrDefault("status", null).toString();
  }

  private GeoLocation decodeLocation(final LoggedRequest request) throws URISyntaxException {
    final Map<String, Object> params = decodeRequest(request);
    final double latitude = Double.parseDouble(params.getOrDefault("lat", "0.0").toString());
    final double longitude = Double.parseDouble(params.getOrDefault("long", "0.0").toString());
    return new GeoLocation(latitude, longitude);
  }

  private Map<String, Object> decodeRequest(final LoggedRequest request) throws URISyntaxException {
    final List<NameValuePair> params =
        URLEncodedUtils.parse(
            new URI(request.getUrl() + "?" + request.getBodyAsString()), StandardCharsets.UTF_8);
    return params.stream()
        .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
  }
}
