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

package com.jdpgrailsdev.oasis.timeline.controller

import com.github.javafaker.Faker
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.data.Post
import com.jdpgrailsdev.oasis.timeline.data.PostException
import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import com.jdpgrailsdev.oasis.timeline.service.PostPublisherService
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.model.Tweet
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

private val logger = KotlinLogging.logger {}

/**
 * Support controller that contains various endpoints used to provide debug or diagnostic
 * information.
 */
@Controller
@RequestMapping("/support")
@SuppressFBWarnings(
  "BC_BAD_CAST_TO_ABSTRACT_COLLECTION",
  "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
  "EI_EXPOSE_REP2",
)
class SupportController(
  private val blueSkyClient: BlueSkyClient,
  private val dateUtils: DateUtils,
  private val faker: Faker,
  private val postFormatUtils: PostFormatUtils,
  private val publishers: List<PostPublisherService<*>>,
  private val timelineDataLoader: TimelineDataLoader,
  private val twitterApiUtils: TwitterApiUtils,
) {
  /**
   * Generates the tweets for a given date.
   *
   * @param dateString A date string in [DateTimeFormatter.ISO_LOCAL_DATE] format.
   * @param target The target social network to receive the post.
   * @return The list of generated tweets for the provided data or an empty list if no events exist.
   */
  @RequestMapping("events")
  @ResponseBody
  fun getEvents(
    @RequestParam("date") dateString: String,
    @RequestParam("target") target: PostTarget,
  ): List<Post> {
    val localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
    val formattedDateString =
      dateUtils.formatDateTime(localDate.atStartOfDay(ZoneId.systemDefault()))
    return timelineDataLoader.getHistory(formattedDateString).mapNotNull { timelineData: TimelineData ->
      convertEventToPost(timelineData, target)
    }
  }

  @RequestMapping("bluesky")
  @ResponseBody
  fun getRecentBlueSkyPosts(): List<String> = blueSkyClient.getPosts()

  @RequestMapping("tweets")
  @ResponseBody
  @Throws(ApiException::class)
  fun getRecentTweets(): List<String> {
    val response =
      twitterApiUtils
        .getTwitterApi()
        .tweets()
        .tweetsRecentSearch("")
        .execute()
    return response.data?.let { data -> data.map { tweet: Tweet -> tweet.text } } ?: emptyList()
  }

  @RequestMapping("user")
  @ResponseBody
  @Throws(ApiException::class)
  fun getTwitterUser(): String {
    val response =
      twitterApiUtils
        .getTwitterApi()
        .users()
        .findMyUser()
        .execute()
    return response.data?.id ?: throw ApiException("User not found.")
  }

  @RequestMapping("publish/events/test/{postTarget}")
  @ResponseBody
  @Throws(PostException::class)
  fun publishTestEventsToSocialNetwork(
    @PathVariable("postTarget") postTarget: PostTarget,
    @RequestParam(value = "type", required = false) type: TimelineDataType?,
  ) {
    val description =
      """Some text with Test Mention and some hash tags #tag1 and #tag2.
${faker.lorem().sentence(postTarget.limit * 3, 0)}"""

    val post =
      postFormatUtils.generatePost(
        description = description,
        timelineDataType = type ?: TimelineDataType.NOTEWORTHY,
        year = Calendar.getInstance().get(Calendar.YEAR),
        postTarget = postTarget,
        additionalContext = emptyList(),
      )
    publishers
      .filter { p: PostPublisherService<*> -> p.getPostTarget() == postTarget }
      .forEach { p: PostPublisherService<*> -> p.publish(post) }
  }

  private fun convertEventToPost(
    timelineData: TimelineData,
    target: PostTarget,
  ): Post? =
    try {
      postFormatUtils.generatePost(
        timelineData,
        timelineDataLoader.getAdditionalHistoryContext(timelineData),
        target,
      )
    } catch (e: PostException) {
      logger.error(e) { "Unable to generate post for timeline data $timelineData." }
      null
    }
}
