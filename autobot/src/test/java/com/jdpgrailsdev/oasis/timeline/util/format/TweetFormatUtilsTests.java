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

package com.jdpgrailsdev.oasis.timeline.util.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import com.jdpgrailsdev.oasis.timeline.config.TemplateContext;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataSource;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import com.jdpgrailsdev.oasis.timeline.data.model.twitter.Tweet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.commons.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

/** Test suite for the {@link TweetFormatUtilsTests} class. */
class TweetFormatUtilsTests {

  private ITemplateEngine templateEngine;

  private TemplateContext templateContext;

  private TweetFormatUtils utils;

  @BeforeEach
  void setup() {
    templateEngine = mock(ITemplateEngine.class);
    templateContext = mock(TemplateContext.class);
    utils = new TweetFormatUtils(templateEngine, templateContext);

    when(templateEngine.process(anyString(), any(IContext.class)))
        .thenReturn("This is a template string.");
    when(templateContext.getHashtags()).thenReturn(Set.of("hashtag1", "hashtag2"));
    when(templateContext.getMentions()).thenReturn(Map.of("A", "a"));
    when(templateContext.getUncapitalizeExclusions()).thenReturn(Set.of("Proper Noun", "Oasis"));
  }

  @Test
  @DisplayName(
      "test that when a event text over the limit is used to generate status updates, a tweet is"
          + " created with multiple parts")
  void testMultipartTweet() throws PublishedEventException {
    final String text = "A word".repeat(200);
    final TimelineData timelineData = mock(TimelineData.class);
    templateEngine = mock(ITemplateEngine.class);
    utils = new TweetFormatUtils(templateEngine, templateContext);

    when(timelineData.getDate()).thenReturn("January 1");
    when(timelineData.getDescription()).thenReturn(text);
    when(timelineData.getSource()).thenReturn(mock(TimelineDataSource.class));
    when(timelineData.getTitle()).thenReturn("title");
    when(timelineData.getType()).thenReturn(TimelineDataType.GIGS);
    when(timelineData.getYear()).thenReturn(2020);
    when(templateEngine.process(anyString(), any(IContext.class))).thenReturn(text);

    final List<String> additionalContext = List.of("additional context");
    final Tweet tweet = utils.generateEvent(timelineData, additionalContext);

    assertNotNull(tweet, AssertionMessage.NON_NULL.toString());
    assertEquals(tweet.getMessages().size(), 6, AssertionMessage.SIZE.toString());
    tweet
        .getMessages()
        .forEach(
            message ->
                assertTrue(
                    message.length() <= Tweet.TWEET_LIMIT, AssertionMessage.LENGTH.toString()));
  }

  @Test
  @DisplayName(
      "test that when a event text under the limit is used to generate status updates, a tweet is"
          + " created a single part")
  void testSinglePartTweet() throws PublishedEventException {
    final String text = "A word";
    final TimelineData timelineData = mock(TimelineData.class);
    templateEngine = mock(ITemplateEngine.class);
    utils = new TweetFormatUtils(templateEngine, templateContext);

    when(timelineData.getDate()).thenReturn("January 1");
    when(timelineData.getDescription()).thenReturn(text);
    when(timelineData.getSource()).thenReturn(mock(TimelineDataSource.class));
    when(timelineData.getTitle()).thenReturn("title");
    when(timelineData.getType()).thenReturn(TimelineDataType.GIGS);
    when(timelineData.getYear()).thenReturn(2020);
    when(templateEngine.process(anyString(), any(IContext.class))).thenReturn(text);

    final List<String> additionalContext = List.of("additional context");
    final Tweet tweet = utils.generateEvent(timelineData, additionalContext);

    assertNotNull(tweet, AssertionMessage.NON_NULL.toString());
    assertEquals(1, tweet.getMessages().size(), AssertionMessage.SIZE.toString());
    tweet
        .getMessages()
        .forEach(
            message ->
                assertTrue(
                    message.length() <= Tweet.TWEET_LIMIT, AssertionMessage.LENGTH.toString()));
  }

  @ParameterizedTest(
      name =
          "test that when a description '#{0}' is prepared for use in the template, the expected"
              + " result '#{1}' is generated")
  @CsvSource({
    "Proper Noun does something., Proper Noun does something",
    "Proper Noun does something, Proper Noun does something",
    "This is a sentence., this is a sentence",
    "This is a sentence, this is a sentence",
    "Oasis are the best, @Oasis are the best",
    "Sentence with Oasis in it, sentence with @Oasis in it",
    ","
  })
  void testPreparingTweetDescription(final String description, final String expected) {
    final String result = utils.prepareDescription(description);
    assertEquals(expected, result, AssertionMessage.VALUE.toString());
  }

  @ParameterizedTest(
      name =
          "test that when mentions are generated for description '{0}' based on mention map {1} ->"
              + " {2}, the expected string of mentions '{3}' is returned")
  @CsvSource({
    "John Doe did something today., john_doe, johndoe, @johndoe",
    "later John Doe did it again., john_doe, johndoe, @johndoe",
    "Jane Doe also did something., john_doe, johndoe, ",
    "John Doe did something today.,,,",
    "John \"Jdoe\" Doe did something., john_jdoe_doe, johndoe, @johndoe",
    "The Queen of England, queen_of_england, hrm_uk, @hrm_uk"
  })
  void testGeneratingMentions(
      final String description,
      final String mentionKey,
      final String mentionValue,
      final String expected) {
    final TemplateContext templateContext = mock(TemplateContext.class);
    final Map<String, String> mentions = new HashMap<>();

    if (StringUtils.isNotBlank(mentionKey)) {
      mentions.put(mentionKey, mentionValue);
    }

    utils = new TweetFormatUtils(templateEngine, templateContext);

    when(templateContext.getMentions()).thenReturn(mentions);

    final String mentionsString = utils.generateMentions(description);

    assertEquals(normalizeMention(expected), mentionsString, AssertionMessage.VALUE.toString());
  }

  private String normalizeMention(final String mention) {
    return StringUtils.isBlank(mention) ? "" : mention;
  }
}
