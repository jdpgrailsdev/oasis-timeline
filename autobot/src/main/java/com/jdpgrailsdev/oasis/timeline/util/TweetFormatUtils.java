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

package com.jdpgrailsdev.oasis.timeline.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jdpgrailsdev.oasis.timeline.config.TweetContext;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.exception.TweetException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

/** A collection of tweet formatting utility methods. */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TweetFormatUtils {

  private static final Logger log = LoggerFactory.getLogger(TweetFormatUtils.class);

  private static final Collection<String> EXCLUDED_TOKENS = Sets.newHashSet("of");

  private static final String NICKNAME_PATTERN = "(\\w+\\s)(\\w+)(\\s\\w+)";

  private final ITemplateEngine textTemplateEngine;

  private final TweetContext tweetContext;

  /**
   * Constructs a new instance.
   *
   * @param textTemplateEngine A Thymeleaf {@link ITemplateEngine} instance used to generate tweets.
   * @param tweetContext A Thymeleaf context used to generate tweets from a template.
   */
  public TweetFormatUtils(
      final ITemplateEngine textTemplateEngine, final TweetContext tweetContext) {
    this.textTemplateEngine = textTemplateEngine;
    this.tweetContext = tweetContext;
  }

  /**
   * Generates a tweet.
   *
   * @param timelineData {@link TimelineData} event to be converted to a tweet.
   * @param additionalContext List of additional context to be included in the tweet.
   * @return The generated tweet.
   * @throws TweetException if unable to generate the tweet.
   */
  public Tweet generateTweet(final TimelineData timelineData, final List<String> additionalContext)
      throws TweetException {
    final Context context =
        new ContextBuilder()
            .withAdditionalContext(String.join(", ", additionalContext).trim())
            .withDescription(prepareDescription(timelineData.getDescription()))
            .withHashtags(
                tweetContext.getHashtags().stream()
                    .map(h -> String.format("#%s", h))
                    .collect(Collectors.joining(" ")))
            .withMentions(generateMentions(timelineData.getDescription()))
            .withType(timelineData.getType())
            .withYear(timelineData.getYear())
            .build();

    final String text = textTemplateEngine.process("tweet", context);
    return new Tweet(text);
  }

  @VisibleForTesting
  String generateMentions(final String description) {
    final List<String> mentions = Lists.newArrayList();
    for (final String key : tweetContext.getMentions().keySet()) {
      log.debug("Converting key '{}' into a searchable name...", key);
      final String name =
          Stream.of(key.split("_")).map(this::formatToken).collect(Collectors.joining(" "));
      final String nameWithQuotes = name.replaceAll(NICKNAME_PATTERN, "$1\"$2\"$3");
      log.debug("Looking for name '{}' in description '{}'...", name, description);
      if (description.contains(name) || description.contains(nameWithQuotes)) {
        log.debug(
            "Match found. Adding '@{}' to list of mentions...",
            tweetContext.getMentions().get(key));
        mentions.add(String.format("@%s", tweetContext.getMentions().get(key)));
      }
    }
    return mentions.stream()
        .sorted(Comparator.comparing(a -> a.toLowerCase(Locale.ENGLISH)))
        .collect(Collectors.joining(" "));
  }

  @VisibleForTesting
  String prepareDescription(final String description) {
    if (StringUtils.hasText(description)) {
      return Mono.just(description)
          .map(this::trimDescription)
          .map(this::uncapitalizeDescription)
          .map(d -> d.replaceAll("Oasis", "@Oasis"))
          .block();
    } else {
      return description;
    }
  }

  private String formatToken(final String token) {
    if (!EXCLUDED_TOKENS.contains(token)) {
      return StringUtils.capitalize(token);
    } else {
      return token;
    }
  }

  private String trimDescription(final String description) {
    if (description.endsWith(".")) {
      return description.substring(0, description.length() - 1).trim();
    } else {
      return description.trim();
    }
  }

  private String uncapitalizeDescription(final String description) {
    if (tweetContext.getUncapitalizeExclusions().stream().noneMatch(description::startsWith)) {
      return StringUtils.uncapitalize(description);
    } else {
      return description;
    }
  }
}
