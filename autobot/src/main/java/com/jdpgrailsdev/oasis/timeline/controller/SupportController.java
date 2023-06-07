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
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetException;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.Get2TweetsSearchRecentResponse;
import com.twitter.clientlib.model.Get2UsersMeResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class SupportController {

  private static final Logger log = LoggerFactory.getLogger(SupportController.class);

  private final DateUtils dateUtils;

  private final TimelineDataLoader timelineDataLoader;

  private final TweetFormatUtils tweetFormatUtils;

  private final TwitterApiUtils twitterApiUtils;

  /**
   * Constructs a new support controller.
   *
   * @param dateUtils The {@link DateUtils} used to format date strings.
   * @param timelineDataLoader The {@link TimelineDataLoader} used to fetch timeline data events.
   * @param tweetFormatUtils The {@link TweetFormatUtils} used to generate a tweet.
   * @param twitterApiUtils The {@link TwitterApiUtils} used to access the Twitter API.
   */
  public SupportController(
      final DateUtils dateUtils,
      final TimelineDataLoader timelineDataLoader,
      final TweetFormatUtils tweetFormatUtils,
      final TwitterApiUtils twitterApiUtils) {
    this.dateUtils = dateUtils;
    this.timelineDataLoader = timelineDataLoader;
    this.tweetFormatUtils = tweetFormatUtils;
    this.twitterApiUtils = twitterApiUtils;
  }

  /**
   * Generates the tweets for a given date.
   *
   * @param dateString A date string in {@link DateTimeFormatter#ISO_LOCAL_DATE} format.
   * @return The list of generated tweets for the provided data or an empty list if no events exist.
   */
  @RequestMapping("events")
  @ResponseBody
  public List<Tweet> getEvents(@RequestParam("date") final String dateString) {
    final LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    final String formattedDateString =
        dateUtils.formatDateTime(localDate.atStartOfDay(ZoneId.systemDefault()));
    return timelineDataLoader.getHistory(formattedDateString).stream()
        .map(this::convertEventToTweet)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @RequestMapping("tweets")
  @ResponseBody
  public List<String> getRecentTweets() throws ApiException {
    final Get2TweetsSearchRecentResponse response =
        twitterApiUtils.getTwitterApi().tweets().tweetsRecentSearch("").execute();
    return response.getData().stream().map(t -> t.getText()).collect(Collectors.toList());
  }

  @RequestMapping("user")
  @ResponseBody
  public String getTwitterUser() throws ApiException {
    final Get2UsersMeResponse response =
        twitterApiUtils.getTwitterApi().users().findMyUser().execute();
    return response.getData().getId();
  }

  private Tweet convertEventToTweet(final TimelineData timelineData) {
    try {
      return tweetFormatUtils.generateTweet(
          timelineData, timelineDataLoader.getAdditionalHistoryContext(timelineData));
    } catch (final TweetException e) {
      log.error("Unable to generate tweet for timeline data {}.", timelineData, e);
      return null;
    }
  }
}
