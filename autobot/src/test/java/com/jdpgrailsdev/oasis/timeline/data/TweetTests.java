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

package com.jdpgrailsdev.oasis.timeline.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import com.jdpgrailsdev.oasis.timeline.util.TweetException;
import com.twitter.clientlib.model.TweetCreateRequest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TweetTests {

  @Test
  @DisplayName("test that when a tweet is created for a blank status, an exception is raised")
  void testExceptionForBlankTweet() {
    Assertions.assertThrows(TweetException.class, () -> new Tweet(null));

    Assertions.assertThrows(TweetException.class, () -> new Tweet(""));
  }

  @Test
  @DisplayName(
      "test that when the main tweet is retrieved, the first tweet in the underlying collection is"
          + " returned")
  void testFirstTweetRetrieved() throws TweetException {
    final String text =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur "
            + "ullamcorper fringilla turpis a dapibus. Proin auctor feugiat rhoncus. Phasellus "
            + "id enim in ex pellentesque cursus sit amet vitae lorem. Aenean eget luctus odio, "
            + "vulputate luctus neque. Aenean non neque non enim laoreet semper. Ut mattis "
            + "lectus imperdiet rhoncus tincidunt. Nam vitae libero lorem. Aenean vulputate "
            + "turpis ac lacus aliquam, et vestibulum erat laoreet. Nullam pretium elit sit "
            + "amet dui maximus, tempor lobortis gravida.";

    final Tweet tweet = new Tweet(text);
    final TweetCreateRequest mainTweet = tweet.getMainTweet();

    assertEquals(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ullamcorper fringilla"
            + " turpis a dapibus. Proin auctor feugiat rhoncus. Phasellus id enim in ex"
            + " pellentesque cursus sit amet vitae lorem. Aenean eget luctus odio, vulputate luctus"
            + " neque. Aenean...",
        mainTweet.getText(),
        AssertionMessage.VALUE.toString());

    final List<String> messages = tweet.getMessages();
    assertEquals(2, messages.size(), AssertionMessage.SIZE.toString());
  }

  @Test
  @DisplayName(
      "test that when an event that exceeds the limit of characters is appropriately broken up into"
          + " individual parts")
  void testSplittingLongTweet() throws TweetException {
    final String text =
        "#OnThisDay in 1994, after back and forth with fans during a gig "
            + "at Riverside in Newcastle, UK, a fight breaks out on stage resulting in Noel "
            + "Gallager damaging a 1960's sunburst Gibson Les Paul guitar given to him by "
            + "Johnny Marr of The Smiths.  The band refuse to continue the show after 5 songs, "
            + "leading to fans surrounding the band's van.  Noel also would require stitches "
            + "after the attack.  The setlist includes the following songs: Columbia, "
            + "Shakermaker, Fade Away, Digsy's Dinner, Live Forever, Bring It On Down "
            + "(Noel Gallagher attacked on stage during song).";

    final Tweet tweet = new Tweet(text);

    assertEquals(3, tweet.getMessages().size(), AssertionMessage.SIZE.toString());
    assertTrue(
        tweet.getMessages().get(0).length() <= Tweet.TWEET_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        "#OnThisDay in 1994, after back and "
            + "forth with fans during a gig at Riverside in Newcastle, UK, a fight breaks out "
            + "on stage resulting in Noel Gallager damaging a 1960's sunburst Gibson Les Paul "
            + "guitar given to him by Johnny Marr of The Smiths.  The...",
        tweet.getMessages().get(0),
        AssertionMessage.VALUE.toString());
    assertTrue(
        tweet.getMessages().get(1).length() <= Tweet.TWEET_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        "... band refuse to continue the show after 5 "
            + "songs, leading to fans surrounding the band's van.  Noel also would require "
            + "stitches after the attack.  The setlist includes the following songs: Columbia, "
            + "Shakermaker, Fade Away, Digsy's Dinner, Live...",
        tweet.getMessages().get(1),
        AssertionMessage.VALUE.toString());
    assertTrue(
        tweet.getMessages().get(2).length() <= Tweet.TWEET_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        "... Forever, Bring It On Down " + "(Noel Gallagher attacked on stage during song).",
        tweet.getMessages().get(2),
        AssertionMessage.VALUE.toString());
  }

  @Test
  @DisplayName(
      "test that when an event exceeds the limit but the split part ends a sentence, the tweet is"
          + " appropriately broken up into individual parts without elipses")
  void testSplitTweetSentenceEnd() throws TweetException {
    final String text =
        TimelineDataType.GIGS.getEmoji()
            + " #OnThisDay in 1991, @Oasis "
            + "perform their first gig under the name \"@Oasis\" at The Boardwalk in "
            + "Manchester, UK.  At this point, the band is a 4-piece made up of Liam "
            + "Gallagher, Paul \"Bonehead\" Arthurs, Paul \"Guigsy\" McGuigan and Tony "
            + "McCarroll.  The Inspiral Carpets are in attendance, accompanied by roadie "
            + "Noel Gallagher, who sees his brother's band perform live for the first "
            + "time.\n\n@liamGallagher @noelgallagher @boneheadspage @TonyMcCarrolls #Oasis "
            + "#TodayInMusic #britpop";
    final Tweet tweet = new Tweet(text);

    assertEquals(2, tweet.getMessages().size(), AssertionMessage.SIZE.toString());
    assertTrue(
        tweet.getMessages().get(0).length() <= Tweet.TWEET_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        TimelineDataType.GIGS.getEmoji()
            + " #OnThisDay "
            + "in 1991, @Oasis perform their first gig under the name \"@Oasis\" at The Boardwalk "
            + "in Manchester, UK.  At this point, the band is a 4-piece made up of Liam Gallagher, "
            + "Paul \"Bonehead\" Arthurs, Paul \"Guigsy\" McGuigan and Tony McCarroll.",
        tweet.getMessages().get(0),
        AssertionMessage.VALUE.toString());
    assertTrue(
        tweet.getMessages().get(1).length() <= Tweet.TWEET_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        "The Inspiral Carpets are in attendance, "
            + "accompanied by roadie Noel Gallagher, who sees his brother's band perform live "
            + "for the first time.\n\n@liamGallagher @noelgallagher @boneheadspage @TonyMcCarrolls "
            + "#Oasis #TodayInMusic #britpop",
        tweet.getMessages().get(1),
        AssertionMessage.VALUE.toString());
  }
}
