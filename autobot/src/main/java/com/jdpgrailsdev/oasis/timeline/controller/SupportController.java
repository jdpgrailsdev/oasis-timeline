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

import com.github.javafaker.Faker;
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient;
import com.jdpgrailsdev.oasis.timeline.data.Post;
import com.jdpgrailsdev.oasis.timeline.data.PostException;
import com.jdpgrailsdev.oasis.timeline.data.PostTarget;
import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import com.jdpgrailsdev.oasis.timeline.service.PostPublisherService;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.MastodonApiUtils;
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils;
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.Get2TweetsSearchRecentResponse;
import com.twitter.clientlib.model.Get2UsersMeResponse;
import com.twitter.clientlib.model.Tweet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import social.bigbone.api.entity.Status;

/**
 * Support controller that contains various endpoints used to provide debug or diagnostic
 * information.
 */
@Controller
@RequestMapping("/support")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class SupportController {

  private static final Logger log = LoggerFactory.getLogger(SupportController.class);

  private final BlueSkyClient blueSkyClient;

  private final DateUtils dateUtils;

  private final Faker faker;

  private final MastodonApiUtils mastodonApiUtils;

  private final PostFormatUtils postFormatUtils;

  private final List<PostPublisherService<?>> publishers;

  private final TimelineDataLoader timelineDataLoader;

  private final TwitterApiUtils twitterApiUtils;

  /**
   * Constructs a new support controller.
   *
   * @param blueSkyClient The {@link BlueSkyClient} used to access the Bluesky API.
   * @param dateUtils The {@link DateUtils} used to format date strings.
   * @param faker The {@link Faker} used to generate text for test events.
   * @param mastodonApiUtils The {@link MastodonApiUtils} used to access the Mastodon API.
   * @param postFormatUtils The {@link PostFormatUtils} used to generate a post.
   * @param publishers The list of {@link PostPublisherService} implementations used to publish a
   *     test post.
   * @param timelineDataLoader The {@link TimelineDataLoader} used to fetch timeline data events.
   * @param twitterApiUtils The {@link TwitterApiUtils} used to access the Twitter API.
   */
  public SupportController(
      final BlueSkyClient blueSkyClient,
      final DateUtils dateUtils,
      final Faker faker,
      final MastodonApiUtils mastodonApiUtils,
      final PostFormatUtils postFormatUtils,
      final List<PostPublisherService<?>> publishers,
      final TimelineDataLoader timelineDataLoader,
      final TwitterApiUtils twitterApiUtils) {
    this.blueSkyClient = blueSkyClient;
    this.dateUtils = dateUtils;
    this.faker = faker;
    this.mastodonApiUtils = mastodonApiUtils;
    this.postFormatUtils = postFormatUtils;
    this.publishers = publishers;
    this.timelineDataLoader = timelineDataLoader;
    this.twitterApiUtils = twitterApiUtils;
  }

  /**
   * Generates the tweets for a given date.
   *
   * @param dateString A date string in {@link DateTimeFormatter#ISO_LOCAL_DATE} format.
   * @param target The target social network to receive the post.
   * @return The list of generated tweets for the provided data or an empty list if no events exist.
   */
  @RequestMapping("events")
  @ResponseBody
  public List<Post> getEvents(
      @RequestParam("date") final String dateString,
      @RequestParam("target") final PostTarget target) {
    final LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    final String formattedDateString =
        dateUtils.formatDateTime(localDate.atStartOfDay(ZoneId.systemDefault()));
    return timelineDataLoader.getHistory(formattedDateString).stream()
        .map(timelineData -> convertEventToPost(timelineData, target))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Returns the posts associated with the {@link PostTarget}.
   *
   * @param postTarget The {@link PostTarget} social network that contains posts.
   * @return The list of associated posts.
   */
  @RequestMapping("posts/{postTarget}")
  @ResponseBody
  public List<String> getRecentPosts(@PathVariable("postTarget") final PostTarget postTarget) {
    return switch (postTarget) {
      case BLUESKY -> blueSkyClient.getPosts();
      case MASTODON -> mastodonApiUtils.getPosts().stream().map(Status::getText).toList();
      case TWITTER -> getRecentTweets();
    };
  }

  @RequestMapping("user")
  @ResponseBody
  public String getTwitterUser() throws ApiException {
    final Get2UsersMeResponse response =
        twitterApiUtils.getTwitterApi().users().findMyUser().execute();
    if (response.getData() != null) {
      return response.getData().getId();
    } else {
      throw new ApiException("User not found.");
    }
  }

  @RequestMapping("publish/events/test/{postTarget}")
  @ResponseBody
  public void publishTestEventsToSocialNetwork(
      @PathVariable("postTarget") final PostTarget postTarget,
      @RequestParam(value = "type", required = false) final TimelineDataType type)
      throws PostException {

    final String description =
        "Some text with Test Mention and some hash tags #tag1 and #tag2."
            + "\n"
            + faker.lorem().sentence(postTarget.getLimit() * 3, 0);

    final Post post =
        postFormatUtils.generatePost(
            description,
            type != null ? type : TimelineDataType.NOTEWORTHY,
            Calendar.getInstance().get(Calendar.YEAR),
            postTarget);
    publishers.stream()
        .filter((p) -> p.getPostTarget() == postTarget)
        .forEach(p -> p.publish(post));
  }

  private Post convertEventToPost(final TimelineData timelineData, final PostTarget target) {
    try {
      return postFormatUtils.generatePost(
          timelineData, timelineDataLoader.getAdditionalHistoryContext(timelineData), target);
    } catch (final PostException e) {
      log.error("Unable to generate post for timeline data {}.", timelineData, e);
      return null;
    }
  }

  private List<String> getRecentTweets() {
    try {
      final Get2TweetsSearchRecentResponse response =
          twitterApiUtils.getTwitterApi().tweets().tweetsRecentSearch("").execute();
      if (response.getData() != null) {
        return response.getData().stream().map(Tweet::getText).collect(Collectors.toList());
      } else {
        return List.of();
      }
    } catch (final ApiException e) {
      log.warn("Unable to retrieve recent tweets.", e);
      return List.of();
    }
  }
}
