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

import com.jdpgrailsdev.oasis.timeline.config.TemplateContext;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import com.jdpgrailsdev.oasis.timeline.data.model.mastodon.MastodonStatus;
import org.thymeleaf.ITemplateEngine;

/** A collection of Mastodon formatting utility methods. */
public class MastodonFormatUtils extends EventFormatUtils<MastodonStatus> {

  /**
   * Constructs a new instance.
   *
   * @param textTemplateEngine A Thymeleaf {@link ITemplateEngine} instance used to generate tweets.
   * @param templateContext A Thymeleaf context used to generate tweets from a template.
   */
  public MastodonFormatUtils(
      final ITemplateEngine textTemplateEngine, final TemplateContext templateContext) {
    super(textTemplateEngine, templateContext);
  }

  @Override
  public MastodonStatus convertToEvent(final String text) throws PublishedEventException {
    return new MastodonStatus(text);
  }

  @Override
  protected String generateMentions(final String description) {
    return "";
  }

  @Override
  protected String getTemplate() {
    return "status";
  }

  @Override
  protected String mentionsReplacement(final String description) {
    return description.replaceAll("Oasis", "#Oasis");
  }
}
