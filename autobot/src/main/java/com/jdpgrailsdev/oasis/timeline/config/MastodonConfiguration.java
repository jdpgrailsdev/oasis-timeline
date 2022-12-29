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

package com.jdpgrailsdev.oasis.timeline.config;

import com.google.gson.Gson;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.MastodonTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.MastodonFormatUtils;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import io.micrometer.core.instrument.MeterRegistry;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.ITemplateEngine;

/** Spring configurations for the Mastodon API. */
@Configuration
public class MastodonConfiguration {

  @Bean
  public OkHttpClient.Builder mastodonClientBuilder() {
    return new OkHttpClient.Builder();
  }

  @Bean
  public MastodonClient mastodonClient(
      @Value("${mastodon.instance.name}") final String mastodonInstanceName,
      @Value("${mastodon.client.access.token}") final String accessToken,
      @Qualifier("mastodonClientBuilder") final OkHttpClient.Builder mastodonClientBuilder) {
    return new MastodonClient.Builder(mastodonInstanceName, mastodonClientBuilder, new Gson())
        .accessToken(accessToken)
        .build();
  }

  @Bean
  public Statuses mastodonStatuses(final MastodonClient mastodonClient) {
    return new Statuses(mastodonClient);
  }

  /**
   * Defines the {@link TemplateContext} bean.
   *
   * @return The {@link TemplateContext} bean.
   */
  @Bean
  @ConfigurationProperties(prefix = "mastodon.context")
  public TemplateContext mastodonContext() {
    return new TemplateContext();
  }

  @Bean
  public MastodonFormatUtils mastodonFormatUtils(
      @Qualifier("textTemplateEngine") final ITemplateEngine templateEngine,
      @Qualifier("mastodonContext") final TemplateContext templateContext) {
    return new MastodonFormatUtils(templateEngine, templateContext);
  }

  /**
   * Defines the {@link MastodonTimelineEventScheduler} bean.
   *
   * @param dateUtils {@link DateUtils} bean.
   * @param meterRegistry Micrometer {@link MeterRegistry} bean.
   * @param timelineDataLoader The {@link TimelineDataLoader} bean.
   * @param mastodonFormatUtils The {@link MastodonFormatUtils} bean.
   * @param mastodonStatuses The {@link Statuses} API client bean.
   * @return The {@link TwitterTimelineEventScheduler} bean.
   */
  @Bean
  public MastodonTimelineEventScheduler mastodonTimelineEventScheduler(
      final DateUtils dateUtils,
      final MeterRegistry meterRegistry,
      final TimelineDataLoader timelineDataLoader,
      final MastodonFormatUtils mastodonFormatUtils,
      final Statuses mastodonStatuses) {
    return new MastodonTimelineEventScheduler(
        dateUtils, mastodonFormatUtils, mastodonStatuses, meterRegistry, timelineDataLoader);
  }
}
