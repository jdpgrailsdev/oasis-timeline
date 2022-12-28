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

import com.google.common.collect.Sets;
import com.jdpgrailsdev.oasis.timeline.config.TemplateContext;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import com.jdpgrailsdev.oasis.timeline.util.ContextBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

/**
 * Collection of common update formatting utilities.
 *
 * @param <T> The typed object that represents a status update on a social platform.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class EventFormatUtils<T> {

  private static final Collection<String> EXCLUDED_TOKENS = Sets.newHashSet("of");

  protected static final String NICKNAME_PATTERN = "(\\w+\\s)(\\w+)(\\s\\w+)";

  private final ITemplateEngine textTemplateEngine;

  private final TemplateContext templateContext;

  /**
   * Constructs a new instance.
   *
   * @param textTemplateEngine A Thymeleaf {@link ITemplateEngine} instance used to generate tweets.
   * @param templateContext A Thymeleaf context used to generate tweets from a template.
   */
  public EventFormatUtils(
      final ITemplateEngine textTemplateEngine, final TemplateContext templateContext) {
    this.textTemplateEngine = textTemplateEngine;
    this.templateContext = templateContext;
  }

  /**
   * Converts the string event message to a typed object which can be used to perform an update via
   * a social platform API.
   *
   * @param text The event message.
   * @return A typed object that represents the event message for a given social platform.
   * @throws PublishedEventException if unable to create the typed object.
   */
  public abstract T convertToEvent(String text) throws PublishedEventException;

  /**
   * Generates an event for publishing.
   *
   * @param timelineData {@link TimelineData} event to be converted to a tweet.
   * @param additionalContext List of additional context to be included in the tweet.
   * @return The generated tweet.
   * @throws PublishedEventException if unable to generate the event.
   */
  public T generateEvent(final TimelineData timelineData, final List<String> additionalContext)
      throws PublishedEventException {
    final Context context =
        new ContextBuilder()
            .withAdditionalContext(String.join(", ", additionalContext).trim())
            .withDescription(prepareDescription(timelineData.getDescription()))
            .withHashtags(
                templateContext.getHashtags().stream()
                    .map(h -> String.format("#%s", h))
                    .collect(Collectors.joining(" ")))
            .withMentions(generateMentions(timelineData.getDescription()))
            .withType(timelineData.getType())
            .withYear(timelineData.getYear())
            .build();

    final String text = textTemplateEngine.process(getTemplate(), context);
    return convertToEvent(text);
  }

  protected String formatToken(final String token) {
    if (!EXCLUDED_TOKENS.contains(token)) {
      return StringUtils.capitalize(token);
    } else {
      return token;
    }
  }

  protected abstract String generateMentions(String description);

  protected abstract String getTemplate();

  protected TemplateContext getTemplateContext() {
    return templateContext;
  }

  protected abstract String mentionsReplacement(String description);

  protected String prepareDescription(final String description) {
    if (StringUtils.hasText(description)) {
      return Mono.just(description)
          .map(this::trimDescription)
          .map(this::uncapitalizeDescription)
          .map(this::mentionsReplacement) // d -> d.replaceAll("Oasis", "@Oasis")
          .block();
    } else {
      return description;
    }
  }

  protected String trimDescription(final String description) {
    if (description.endsWith(".")) {
      return description.substring(0, description.length() - 1).trim();
    } else {
      return description.trim();
    }
  }

  protected String uncapitalizeDescription(final String description) {
    if (templateContext.getUncapitalizeExclusions().stream().noneMatch(description::startsWith)) {
      return StringUtils.uncapitalize(description);
    } else {
      return description;
    }
  }
}
