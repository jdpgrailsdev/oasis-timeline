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
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.jdpgrailsdev.oasis.timeline.config.IntegrationTestConfiguration;
import com.jdpgrailsdev.oasis.timeline.context.StartupApplicationListener;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.exception.SecurityException;
import com.jdpgrailsdev.oasis.timeline.mocks.MockDateUtils;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.service.DataStoreService;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.JSON;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.model.TweetCreateRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

@ActiveProfiles("test")
@SpringBootTest(classes = {IntegrationTestConfiguration.class})
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {WireMockInitializer.class})
class EndToEndIntegrationTests {

  private static final String AUTH_USERNAME = "test";
  private static final String AUTH_PASSWORD = "test";
  private static final String SIZE_ASSERTION_MESSAGE = "expected number of tweets produced";
  private static final String TWEET_BODY_MESSAGE = "expected tweet message body";
  private static final String TWITTER_OAUTH2_RESPONSE_FILE = "/twitter_oauth2_response.json";
  private static final String TWITTER_OAUTH2_CALLBACK_URL = "/2/oauth2/callback";
  private static final String TWITTER_OAUTH2_TOKEN_URI = "/2/oauth2/token";
  private static final String TWITTER_RESPONSE_FILE = "/twitter_response.json";
  private static final String TWITTER_URI = "/2/tweets";

  @Autowired private TwitterTimelineEventScheduler scheduler;
  @Autowired private MockDateUtils dateUtils;
  @Autowired private DataStoreService dataStoreService;
  @Autowired private MockMvc mockMvc;

  @Value("${spring.data.redis.prefix}")
  private String prefix;

  @Autowired private RedisTemplate<String, String> redisTemplate;
  @Autowired private StartupApplicationListener startupApplicationListener;
  @Autowired private TwitterApiUtils twitterApiUtils;
  @Autowired private TwitterCredentialsOAuth2 twitterCredentials;
  @Autowired private WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    WireMock.configureFor("localhost", 9093);
  }

  @AfterAll // you're my Wonderwall
  static void tearDown() {
    WireMock.shutdownServer();
  }

  @AfterEach
  void cleanup() {
    dateUtils.reset();
    wireMockServer.resetAll();
    redisTemplate.delete(DataStoreService.generateKey(prefix, TwitterApiUtils.ACCESS_TOKEN_KEY));
    redisTemplate.delete(DataStoreService.generateKey(prefix, TwitterApiUtils.REFRESH_TOKEN_KEY));
    twitterCredentials.setTwitterOauth2AccessToken(null);
    twitterCredentials.setTwitterOauth2RefreshToken(null);
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

    verify(7, postRequestedFor(urlEqualTo(TWITTER_URI)));

    final List<ServeEvent> serveEventList = getAllServeEvents();
    assertEquals(7, serveEventList.size(), SIZE_ASSERTION_MESSAGE);

    final String tweet1 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1994, Noel Gallagher, Liam Gallagher and Paul \"Bonehead\" "
            + "Arthurs appear on MTV's Most Wanted to promote the release of their upcoming "
            + "debut album 'Definitely Maybe' and a live show later that evening at the "
            + "Astoria Theatre in...";
    validateTweet(tweet1, serveEventList.get(6).getRequest());

    final String tweet2 =
        "... London, UK.  After a short "
            + "interview with host Davina McCall, the trio perform 'Whatever' and 'Live "
            + "Forever'.  The performance is notable as Bonehead accompanies Noel and Liam on "
            + "the piano instead of his customary rhythm guitar.\n\n@boneheadspage...";
    validateTweet(tweet2, serveEventList.get(5).getRequest());

    final String tweet3 =
        "... @liamGallagher @noelgallagher " + "@ThisisDavina #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet3, serveEventList.get(4).getRequest());

    final String tweet4 =
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1996, Noel Gallagher joins Paul Weller and Jools Holland on "
            + "stage at the first V Festival in Hylands Park, Chelmsford, UK to play 'Eye of "
            + "the Storm'.  During the song, Noel asks the crowd to \"show your appreciation\" "
            + "and informs the...";
    validateTweet(tweet4, serveEventList.get(3).getRequest());

    final String tweet5 =
        "... crowd that \"Alan White's brother\" (Steve White) is on drums.  Paul Weller returns"
            + " the favor by thanking \"Mr. Liam Gallagher\" after the jam.  It would be another"
            + " nine years before @Oasis would finally appear at the festival in 2005.\n\n"
            + "@drummerwhitey...";
    validateTweet(tweet5, serveEventList.get(2).getRequest());

    final String tweet6 =
        "... @liamGallagher @noelgallagher @paulwellerHQ #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet6, serveEventList.get(1).getRequest());

    final String tweet7 =
        TimelineDataType.CERTIFICATIONS.getEmoji()
            + " #OnThisDay in 2022, the British Phonographic Institute certifies 'Time Flies"
            + " 1994-2009' album sales as 5x platinum.\n"
            + "\n"
            + "@bpi_music #Oasis #OTD #TodayInMusic #britpop";
    validateTweet(tweet7, serveEventList.get(0).getRequest());
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

  @Test
  void testCredentialLoadOnStartup() throws SecurityException {
    final String accessToken = "accessToken";
    final String refreshToken = "refreshToken";
    dataStoreService.setValue(TwitterApiUtils.ACCESS_TOKEN_KEY, accessToken);
    dataStoreService.setValue(TwitterApiUtils.REFRESH_TOKEN_KEY, refreshToken);

    // Check the credentials before the listener runs to assert that they are not yet retrieved/set
    final TwitterCredentialsOAuth2 twitterCredentialsBefore =
        twitterApiUtils.getTwitterCredentials();
    assertFalse(StringUtils.hasText(twitterCredentialsBefore.getTwitterOauth2AccessToken()));
    assertFalse(StringUtils.hasText(twitterCredentialsBefore.getTwitterOauth2RefreshToken()));

    startupApplicationListener.onApplicationEvent(mock(ContextRefreshedEvent.class));

    // Check the credentials after the listener urns to assert that they are now set in memory
    final TwitterCredentialsOAuth2 twitterCredentialsAfter =
        twitterApiUtils.getTwitterCredentials();
    assertEquals(accessToken, twitterCredentialsAfter.getTwitterOauth2AccessToken());
    assertEquals(refreshToken, twitterCredentialsAfter.getTwitterOauth2RefreshToken());
  }

  @Test
  void testCredentialLoadOnStartupCredentialsNotInDataStore() {
    final TwitterCredentialsOAuth2 twitterCredentialsBefore =
        twitterApiUtils.getTwitterCredentials();
    assertFalse(StringUtils.hasText(twitterCredentialsBefore.getTwitterOauth2AccessToken()));
    assertFalse(StringUtils.hasText(twitterCredentialsBefore.getTwitterOauth2RefreshToken()));

    startupApplicationListener.onApplicationEvent(mock(ContextRefreshedEvent.class));

    final TwitterCredentialsOAuth2 twitterCredentialsAfter =
        twitterApiUtils.getTwitterCredentials();
    assertFalse(StringUtils.hasText(twitterCredentialsAfter.getTwitterOauth2AccessToken()));
    assertFalse(StringUtils.hasText(twitterCredentialsAfter.getTwitterOauth2RefreshToken()));
  }

  @Test
  void testCredentialsWrittenToDataStoreOnRefresh() throws ApiException, SecurityException {
    final String accessToken = "accessToken";
    final String refreshToken = "refreshToken";
    final OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);

    when(oAuth2AccessToken.getAccessToken()).thenReturn(accessToken);
    when(oAuth2AccessToken.getRefreshToken()).thenReturn(refreshToken);

    final TwitterCredentialsOAuth2 twitterCredentialsBefore =
        twitterApiUtils.getTwitterCredentials();
    assertFalse(StringUtils.hasText(twitterCredentialsBefore.getTwitterOauth2AccessToken()));
    assertFalse(StringUtils.hasText(twitterCredentialsBefore.getTwitterOauth2RefreshToken()));

    twitterApiUtils.updateAccessTokens(oAuth2AccessToken);

    final Optional<String> updatedAccessToken =
        dataStoreService.getValue(TwitterApiUtils.ACCESS_TOKEN_KEY);
    final Optional<String> updatedRefreshToken =
        dataStoreService.getValue(TwitterApiUtils.REFRESH_TOKEN_KEY);

    assertTrue(updatedAccessToken.isPresent());
    assertTrue(updatedRefreshToken.isPresent());
    assertEquals(accessToken, updatedAccessToken.get());
    assertEquals(refreshToken, updatedRefreshToken.get());

    final TwitterCredentialsOAuth2 twitterCredentialsAfter =
        twitterApiUtils.getTwitterCredentials();
    assertEquals(accessToken, twitterCredentialsAfter.getTwitterOauth2AccessToken());
    assertEquals(refreshToken, twitterCredentialsAfter.getTwitterOauth2RefreshToken());
  }

  @Test
  @WithMockUser(username = AUTH_USERNAME, password = AUTH_PASSWORD)
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testManualAuthorization() throws Exception {
    mockMvc
        .perform(get("/oauth2/authorize"))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            header()
                .string(
                    LOCATION,
                    "https://twitter.com/i/oauth2/authorize?code_challenge=challenge&code_challenge_method=PLAIN&response_type=code&client_id=clientid&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fcallback&scope=offline.access%20tweet.read%20tweet.write%20users.read&state=state"));
  }

  @Test
  @WithMockUser(username = AUTH_USERNAME, password = AUTH_PASSWORD)
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testManualAuthorizationCallback() throws Exception {
    final String oauth2Response =
        new String(
            Files.readAllBytes(
                new ClassPathResource(TWITTER_OAUTH2_RESPONSE_FILE).getFile().toPath()));

    stubFor(post(urlEqualTo(TWITTER_OAUTH2_TOKEN_URI)).willReturn(okJson(oauth2Response)));
    stubFor(post(urlEqualTo(TWITTER_OAUTH2_CALLBACK_URL)).willReturn(okJson(oauth2Response)));

    mockMvc
        .perform(get("/oauth2/callback?code={code}", "code"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("OK")));
  }

  private void validateTweet(final String tweet, final LoggedRequest loggedRequest)
      throws URISyntaxException {
    final String request = decodeTweet(loggedRequest);
    assertEquals(tweet, request, TWEET_BODY_MESSAGE);
  }

  private String decodeTweet(final LoggedRequest request) throws URISyntaxException {
    final TweetCreateRequest tweetCreateRequest = decodeRequest(request);
    return tweetCreateRequest.getText();
  }

  private TweetCreateRequest decodeRequest(final LoggedRequest request) throws URISyntaxException {
    return JSON.deserialize(request.getBodyAsString(), TweetCreateRequest.class);
  }
}
