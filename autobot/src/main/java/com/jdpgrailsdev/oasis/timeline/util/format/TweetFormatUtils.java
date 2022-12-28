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

import com.google.common.collect.Lists;
import com.jdpgrailsdev.oasis.timeline.config.TemplateContext;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import com.jdpgrailsdev.oasis.timeline.data.model.twitter.Tweet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;

/** A collection of tweet formatting utility methods. */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TweetFormatUtils extends EventFormatUtils<Tweet> {

  private static final Logger log = LoggerFactory.getLogger(TweetFormatUtils.class);

  /**
   * Constructs a new instance.
   *
   * @param textTemplateEngine A Thymeleaf {@link ITemplateEngine} instance used to generate tweets.
   * @param templateContext A Thymeleaf context used to generate tweets from a template.
   */
  public TweetFormatUtils(
      final ITemplateEngine textTemplateEngine, final TemplateContext templateContext) {
    super(textTemplateEngine, templateContext);
  }

  @Override
  public Tweet convertToEvent(final String text) throws PublishedEventException {
    return new Tweet(text);
  }

  @Override
  public String generateMentions(final String description) {
    final List<String> mentions = Lists.newArrayList();
    for (final String key : getTemplateContext().getMentions().keySet()) {
      log.debug("Converting key '{}' into a searchable name...", key);
      final String name =
          Stream.of(key.split("_")).map(this::formatToken).collect(Collectors.joining(" "));
      final String nameWithQuotes = name.replaceAll(NICKNAME_PATTERN, "$1\"$2\"$3");
      log.debug("Looking for name '{}' in description '{}'...", name, description);
      if (description.contains(name) || description.contains(nameWithQuotes)) {
        log.debug(
            "Match found. Adding '@{}' to list of mentions...",
            getTemplateContext().getMentions().get(key));
        mentions.add(String.format("@%s", getTemplateContext().getMentions().get(key)));
      }
    }
    return mentions.stream()
        .sorted(Comparator.comparing(a -> a.toLowerCase(Locale.ENGLISH)))
        .collect(Collectors.joining(" "));
  }

  @Override
  public String getTemplate() {
    return "tweet";
  }

  @Override
  public String mentionsReplacement(final String description) {
    return description.replaceAll("Oasis", "@Oasis");
  }
}
