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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jdpgrailsdev.oasis.timeline.config.IntegrationTestConfiguration;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.mocks.MockDateUtils;
import com.jdpgrailsdev.oasis.timeline.mocks.MockTwitter;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = {IntegrationTestConfiguration.class})
class EndToEndIntegrationTests {

  private static final String SIZE_ASSERTION_MESSAGE = "expected number of tweets produced";

  private static final String TWEET_BODY_MESSAGE = "expected tweet message body";

  @Autowired private TwitterTimelineEventScheduler scheduler;

  @Autowired private MockTwitter twitter;

  @Autowired private MockDateUtils dateUtils;

  @AfterEach
  void cleanup() {
    dateUtils.reset();
    twitter.reset();
  }

  @Test
  @DisplayName(
      "test that when the scheduler is invoked for a date with timeline events, the events are"
          + " published to Twitter")
  void testSchedulingTweets() {
    dateUtils.setToday("October 2");
    scheduler.publishTimelineTweet();

    assertEquals(twitter.getTweets().size(), 3, SIZE_ASSERTION_MESSAGE);
    assertEquals(
        TimelineDataType.RELEASES.getEmoji()
            + " #OnThisDay in 1995, @Oasis release '(What's the Story) Morning Glory?', their "
            + "second studio album, on Creation Records.  The album would propel the band to a "
            + "worldwide fame, selling over 12 million copies around the world."
            + "\n\n@creationrecords #Oasis #OTD #TodayInMusic #britpop",
        twitter.getTweets().get(0),
        TWEET_BODY_MESSAGE);
    assertEquals(
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 2016, '@Oasis: Supersonic' premieres at the Manchester Odeon "
            + "Printworks in Manchester, UK.  The event is attended by Liam Gallagher, Paul "
            + "\"Bonehead\" Arthurs and director Mat Whitecross.  Liam, Bonehead and Mat take "
            + "part in a Q&A with the audience after...",
        twitter.getTweets().get(1),
        TWEET_BODY_MESSAGE);
    assertEquals(
        "... the screening of the film."
            + "\n\n@boneheadspage @liamGallagher @matwhitecross #Oasis #OTD #TodayInMusic #britpop",
        twitter.getTweets().get(2),
        TWEET_BODY_MESSAGE);
  }

  @Test
  @DisplayName(
      "test that when the scheduler is invoked for a date with timeline events and the generated"
          + " tweet exceeds the size limit, the event is split and published to Twitter")
  void testSchedulingTweetsExceedingTheLimit() {
    dateUtils.setToday("April 24");
    scheduler.publishTimelineTweet();

    assertEquals(twitter.getTweets().size(), 3, SIZE_ASSERTION_MESSAGE);
    assertEquals(
        TimelineDataType.RELEASES.getEmoji()
            + " #OnThisDay in 1995, @Oasis release 'Some Might Say', the first single from "
            + "the forthcoming album '(What's The Story) Morning Glory?', on Creation "
            + "Records.  It would go on to become @Oasis' first number one single and is "
            + "the last recording to...",
        twitter.getTweets().get(0),
        TWEET_BODY_MESSAGE);
    assertEquals(
        twitter.getTweets().get(1),
        "... feature the original lineup.  The single "
            + "includes the b-sides 'Talk Tonight', 'Acquiesce' and 'Headshrinker'."
            + "\n\n@creationrecords #Oasis #OTD #TodayInMusic #britpop",
        TWEET_BODY_MESSAGE);
  }

  @Test
  @DisplayName(
      "test that when the scheduler is invoked for a date with timeline events that should include"
          + " mentions, the events are published to Twitter with the mentions included")
  void testSchedulingTweetsWithMentions() {
    dateUtils.setToday("August 18");
    scheduler.publishTimelineTweet();

    assertEquals(twitter.getTweets().size(), 8, SIZE_ASSERTION_MESSAGE);
    assertEquals(
        TimelineDataType.GIGS.getEmoji()
            + " #OnThisDay in 1991, @Oasis perform their first gig under the name \"@Oasis\" "
            + "at The Boardwalk in Manchester, UK.  At this point, the band is a 4-piece made "
            + "up of Liam Gallagher, Paul \"Bonehead\" Arthurs, Paul \"Guigsy\" McGuigan and "
            + "Tony McCarroll.",
        twitter.getTweets().get(0),
        TWEET_BODY_MESSAGE);
    assertEquals(
        "The Inspiral Carpets are in attendance, "
            + "accompanied by roadie Noel Gallagher, who sees his brother's band perform live "
            + "for the first time.\n\n@boneheadspage @liamGallagher @noelgallagher "
            + "@TonyMcCarrolls #Oasis #OTD #TodayInMusic #britpop",
        twitter.getTweets().get(1),
        TWEET_BODY_MESSAGE);
    assertEquals(
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1994, Noel Gallagher, Liam Gallagher and Paul \"Bonehead\" "
            + "Arthurs appear on MTV's Most Wanted to promote the release of their upcoming "
            + "debut album 'Definitely Maybe' and a live show later that evening at the "
            + "Kentish Town Forum in...",
        twitter.getTweets().get(2),
        TWEET_BODY_MESSAGE);
    assertEquals(
        "... London, UK.  After a short "
            + "interview with host Davina McCall, the trio perform 'Whatever' and 'Live "
            + "Forever'.  The performance is notable as Bonehead accompanies Noel and Liam on "
            + "the piano instead of his customary rhythm guitar.\n\n@boneheadspage...",
        twitter.getTweets().get(3),
        TWEET_BODY_MESSAGE);
    assertEquals(
        "... @liamGallagher @noelgallagher " + "@ThisisDavina #Oasis #OTD #TodayInMusic #britpop",
        twitter.getTweets().get(4),
        TWEET_BODY_MESSAGE);
    assertEquals(
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1996, Noel Gallagher joins Paul Weller and Jools Holland on "
            + "stage at the first V Festival in Hylands Park, Chelmsford, UK to play 'Eye of "
            + "the Storm'.  During the song, Noel asks the crowd to \"show your appreciation\" "
            + "and informs...",
        twitter.getTweets().get(5),
        TWEET_BODY_MESSAGE);
    assertEquals(
        "... the crowd that \"Alan White's "
            + "brother\" (Steve White) is on drums.  Paul Weller returns the favor "
            + "by thanking \"Mr. Liam Gallagher\" after the jam.  It would be another "
            + "nine years before @Oasis would finally appear at the festival in...",
        twitter.getTweets().get(6),
        TWEET_BODY_MESSAGE);
    assertEquals(
        "... 2005.\n\n@drummerwhitey "
            + "@liamGallagher @noelgallagher #Oasis #OTD #TodayInMusic #britpop",
        twitter.getTweets().get(7),
        TWEET_BODY_MESSAGE);
  }

  @Test
  @DisplayName(
      "test that when the scheduled task is invoked for each date on the calendar, the task is"
          + " executed")
  void testSchedulingSpecificDate() {
    final LocalDate end = LocalDate.of(2021, 1, 1);
    for (LocalDate date = LocalDate.of(2020, 1, 1); date.isBefore(end); date = date.plusDays(1)) {
      final ZonedDateTime localDate = date.atStartOfDay(ZoneId.systemDefault());
      final String today =
          localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
              + " "
              + localDate.getDayOfMonth();

      dateUtils.setToday(today);
      scheduler.publishTimelineTweet();

      assertTrue(twitter.getTweets().size() > 0, SIZE_ASSERTION_MESSAGE);
      twitter
          .getTweets()
          .forEach(
              tweet -> assertTrue(tweet.length() <= Tweet.TWEET_LIMIT, SIZE_ASSERTION_MESSAGE));
    }
  }

  @Test
  @DisplayName("test that Twitter handle replacement works for names that end with an apostrophe")
  void testTwitterHandleReplacement() {
    dateUtils.setToday("May 7");
    scheduler.publishTimelineTweet();

    assertEquals(twitter.getTweets().size(), 1, "expected number of tweets produced");
    assertEquals(
        TimelineDataType.NOTEWORTHY.getEmoji()
            + " #OnThisDay in 1996, @Oasis appear on Chris Evans' Radio 1 Breakfast Show and "
            + "announce that they will be playing two nights at Knebworth in August."
            + "\n\n@achrisevans #Oasis #OTD #TodayInMusic #britpop",
        twitter.getTweets().get(0),
        TWEET_BODY_MESSAGE);
  }
}
