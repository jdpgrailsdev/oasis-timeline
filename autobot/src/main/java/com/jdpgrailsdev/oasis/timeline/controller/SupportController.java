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

package com.jdpgrailsdev.oasis.timeline.controller;

import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import com.jdpgrailsdev.oasis.timeline.data.model.mastodon.MastodonStatus;
import com.jdpgrailsdev.oasis.timeline.data.model.twitter.Tweet;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.MastodonFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.format.TweetFormatUtils;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Support controller that contains various endpoints used to provide debug or diagnostic
 * information.
 */
@Controller
@RequestMapping("/support")
public class SupportController {

  private static final Logger log = LoggerFactory.getLogger(SupportController.class);

  private final DateUtils dateUtils;

  private final MastodonFormatUtils mastodonFormatUtils;

  private final TimelineDataLoader timelineDataLoader;

  private final TweetFormatUtils tweetFormatUtils;

  /**
   * Constructs a new support controller.
   *
   * @param dateUtils The {@link DateUtils} used to format date strings.
   * @param mastodonFormatUtils The {@link MastodonFormatUtils} used to generate a status update.
   * @param timelineDataLoader The {@link TimelineDataLoader} used to fetch timeline data events.
   * @param tweetFormatUtils The {@link TweetFormatUtils} used to generate a tweet.
   */
  public SupportController(
      final DateUtils dateUtils,
      final MastodonFormatUtils mastodonFormatUtils,
      final TimelineDataLoader timelineDataLoader,
      final TweetFormatUtils tweetFormatUtils) {
    this.dateUtils = dateUtils;
    this.mastodonFormatUtils = mastodonFormatUtils;
    this.timelineDataLoader = timelineDataLoader;
    this.tweetFormatUtils = tweetFormatUtils;
  }

  /**
   * Generates the tweets for a given date.
   *
   * @param dateString A date string in {@link DateTimeFormatter#ISO_LOCAL_DATE} format.
   * @return The list of generated tweets for the provided data or an empty list if no events exist.
   */
  @RequestMapping("events/tweet")
  @ResponseBody
  public List<Tweet> getTwitterEvents(@RequestParam("date") final String dateString) {
    final LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    final String formattedDateString =
        dateUtils.formatDateTime(localDate.atStartOfDay(ZoneId.systemDefault()));
    return timelineDataLoader.getHistory(formattedDateString).stream()
        .map(this::convertEventToTweet)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Generates the Mastodon status update for a given date.
   *
   * @param dateString A date string in {@link DateTimeFormatter#ISO_LOCAL_DATE} format.
   * @return The list of generated Mastodon status updates for the provided data or an empty list if
   *     no events exist.
   */
  @RequestMapping("events/mastodon")
  @ResponseBody
  public List<MastodonStatus> getMastodonEvents(@RequestParam("date") final String dateString) {
    final LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    final String formattedDateString =
        dateUtils.formatDateTime(localDate.atStartOfDay(ZoneId.systemDefault()));
    return timelineDataLoader.getHistory(formattedDateString).stream()
        .map(this::convertEventToMastodonStatus)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private Tweet convertEventToTweet(final TimelineData timelineData) {
    try {
      return tweetFormatUtils.generateEvent(
          timelineData, timelineDataLoader.getAdditionalHistoryContext(timelineData));
    } catch (final PublishedEventException e) {
      log.error("Unable to generate tweet for timeline data {}.", timelineData, e);
      return null;
    }
  }

  private MastodonStatus convertEventToMastodonStatus(final TimelineData timelineData) {
    try {
      return mastodonFormatUtils.generateEvent(
          timelineData, timelineDataLoader.getAdditionalHistoryContext(timelineData));
    } catch (final PublishedEventException e) {
      log.error("Unable to generate Mastodon status update for timeline data {}.", timelineData, e);
      return null;
    }
  }
}
