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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jdpgrailsdev.oasis.timeline.exception.TweetException;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateRequestReply;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

/** Represents a Twitter tweet message. */
public class Tweet {

  public static final Integer TWEET_LIMIT = 280;

  /** Accounts for the emoji and elipses added to multi-part tweets. */
  private static final Integer ADDITIONAL_CHARACTERS = 4;

  private static final Double REDUCTION_PERCENTAGE = 0.85;

  private final List<String> messages;

  /**
   * Creates a new tweet.
   *
   * @param text The text of the tweet.
   * @throws TweetException if the provided text is blank.
   */
  public Tweet(final String text) throws TweetException {

    if (StringUtils.hasText(text)) {
      if (text.length() > TWEET_LIMIT) {
        messages = splitTweet(text);
      } else {
        messages = Lists.newArrayList(text);
      }
    } else {
      throw new TweetException("Tweet message may not be blank.");
    }
  }

  /**
   * Retrieves the messages associated with this tweet. Tweets may have multiple parts.
   *
   * @return The message parts associated with the tweet.
   */
  public List<String> getMessages() {
    return ImmutableList.copyOf(messages);
  }

  @JsonIgnore
  public TweetCreateRequest getMainTweet() {
    return createTweet(messages.stream().findFirst().orElse(""), null);
  }

  /**
   * Returns the replies for the main tweet if the message exceeds the tweet limit.
   *
   * @param inReplyToStatusId The ID of the main tweet.
   * @return The replies to the main tweet or an empty list if no replies are necessary.
   */
  @JsonIgnore
  public List<TweetCreateRequest> getReplies(final String inReplyToStatusId) {
    return messages.stream()
        .skip(1)
        .map(r -> createTweet(r, inReplyToStatusId))
        .collect(Collectors.toList());
  }

  private TweetCreateRequest createTweet(final String text, final String inReplyToStatusId) {
    final TweetCreateRequest tweetCreateRequest = new TweetCreateRequest();
    tweetCreateRequest.setText(text.trim());
    if (StringUtils.hasText(inReplyToStatusId)) {
      final TweetCreateRequestReply reply = new TweetCreateRequestReply();
      reply.setInReplyToTweetId(inReplyToStatusId);
      tweetCreateRequest.setReply(reply);
    }

    return tweetCreateRequest;
  }

  private List<String> splitTweet(final String text) {
    final List<String> tweets = Lists.newArrayList();
    int size = text.length();

    while (size >= (TWEET_LIMIT - ADDITIONAL_CHARACTERS)) {
      size = (int) Math.floor(size * REDUCTION_PERCENTAGE);
    }

    final StringBuilder builder = new StringBuilder();
    final List<String> words = Splitter.on(' ').splitToList(text);
    for (final String word : words) {
      if ((builder.length() + word.length()) <= size) {
        builder.append(' ');
      } else {
        final boolean useEllipses = !builder.toString().trim().endsWith(".");
        if (useEllipses) {
          builder.append("...");
        }
        tweets.add(builder.toString().trim());
        builder.setLength(0);
        if (useEllipses) {
          builder.append("... ");
        }
      }
      builder.append(word);
    }
    tweets.add(builder.toString().trim());
    return tweets;
  }
}
