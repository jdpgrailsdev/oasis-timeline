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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jdpgrailsdev.oasis.timeline.config.TweetContext;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.Tweet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Mono;
import twitter4j.TwitterException;

public class TweetFormatUtils {

    private static final Logger log = LoggerFactory.getLogger(TweetFormatUtils.class);

    private static final Collection<String> EXCLUDED_TOKENS = Sets.newHashSet("of");

    private static final String NICKNAME_PATTERN = "(\\w+\\s)(\\w+)(\\s\\w+)";

    private final ITemplateEngine textTemplateEngine;

    private final TweetContext tweetContext;

    public TweetFormatUtils(final ITemplateEngine textTemplateEngine, final TweetContext tweetContext) {
        this.textTemplateEngine = textTemplateEngine;
        this.tweetContext = tweetContext;
    }

    public Tweet generateTweet(final TimelineData timelineData, final List<String> additionalContext) throws TwitterException {
        final Context context = new ContextBuilder()
                .withAdditionalContext(additionalContext.stream().collect(Collectors.joining(", ")).trim())
                .withDescription(prepareDescription(timelineData.getDescription()))
                .withHashtags(tweetContext.getHashtags().stream().map(h -> String.format("#%s", h)).collect(Collectors.joining(" ")))
                .withMentions(generateMentions(timelineData.getDescription()))
                .withType(timelineData.getType())
                .withYear(timelineData.getYear())
                .build();

        final String text = textTemplateEngine.process("tweet", context);
        return new Tweet(text);
    }

    private String generateMentions(final String description) {
        final List<String> mentions = Lists.newArrayList();
        for(final String key : tweetContext.getMentions().keySet()) {
            log.debug("Converting key '{}' into a searchable name...", key);
            final String name = Stream.of(key.split("_")).map(this::formatToken).collect(Collectors.joining(" "));
            final String nameWithQuotes = name.replaceAll(NICKNAME_PATTERN, "$1\"$2\"$3");
            log.debug("Looking for name '{}' in description '{}'...", name, description);
            if(description.contains(name) || description.contains(nameWithQuotes)) {
                log.debug("Match found. Adding '@{}' to list of mentions...", tweetContext.getMentions().get(key));
                mentions.add(String.format("@%s",tweetContext.getMentions().get(key)));
            }
        }
        return mentions.stream()
                .sorted((a,b) -> a.toLowerCase(Locale.ENGLISH).compareTo(b.toLowerCase(Locale.ENGLISH)))
                .collect(Collectors.joining(" "));
    }

    private String prepareDescription(final String description) {
        if(StringUtils.hasText(description)) {
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
        if(!EXCLUDED_TOKENS.contains(token)) {
            return StringUtils.capitalize(token);
        } else {
            return token;
        }
    }

    private String trimDescription(final String description) {
        if(description.endsWith(".")) {
            return description.substring(0, description.length() - 1).trim();
        } else {
            return description.trim();
        }
    }

    private String uncapitalizeDescription(final String description) {
        if(tweetContext.getUncapitalizeExclusions().stream().filter(exclusion -> description.startsWith(exclusion)).count() == 0) {
            return StringUtils.uncapitalize(description);
        } else {
            return description;
        }
    }
}
