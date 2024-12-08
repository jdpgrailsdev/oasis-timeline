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

package com.jdpgrailsdev.oasis.timeline.service

import com.jdpgrailsdev.oasis.timeline.client.BlueSkyClient
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyCreateRecordResponse
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReplyPost
import com.jdpgrailsdev.oasis.timeline.client.toReplyPost
import com.jdpgrailsdev.oasis.timeline.data.Post
import com.jdpgrailsdev.oasis.timeline.data.PostException
import com.jdpgrailsdev.oasis.timeline.data.PostResponse
import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.data.createPostResponse
import com.jdpgrailsdev.oasis.timeline.util.BlueSkyUtils
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.PostFormatUtils
import com.jdpgrailsdev.oasis.timeline.util.TwitterApiUtils
import com.jdpgrailsdev.oasis.timeline.util.toBlueskyRecord
import com.jdpgrailsdev.oasis.timeline.util.toTweetCreateRequest
import com.jdpgrailsdev.oasis.timeline.util.toTweetReplies
import com.newrelic.api.agent.NewRelic
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.model.TweetCreateRequest
import com.twitter.clientlib.model.TweetCreateResponse
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.util.CollectionUtils
import reactor.core.publisher.Flux

const val PUBLISH_EXECUTIONS = "scheduledTimelinePostPublish"
const val TIMELINE_EVENTS_PUBLISHED = "timelineEventsPublished"
const val TIMELINE_EVENTS_PUBLISHED_FAILURES = "timelineEventsPublishedFailures"

private val logger = KotlinLogging.logger {}

/**
 * Generic representation of a social post publisher. This class is responsible for publishing a
 * [Post] to a specific social network by converting one or more [TimelineData] events into the
 * correct post format for the target social network.
 */
@SuppressFBWarnings(value = ["EI_EXPOSE_REP2", "BC_BAD_CAST_TO_ABSTRACT_COLLECTION"])
abstract class PostPublisherService<T>(
  protected val dateUtils: DateUtils,
  protected val meterRegistry: MeterRegistry,
  private val postFormatUtils: PostFormatUtils,
  private val timelineDataLoader: TimelineDataLoader,
) {
  /**
   * Returns the social network target for the publisher.
   *
   * @return The [PostTarget] of the publisher.
   */
  abstract fun getPostTarget(): PostTarget

  /**
   * Publishes a [Post] to the target social network.
   *
   * @param post The [Post] to publish.
   * @return The [PostResponse] wrapper around the social network API response.
   */
  abstract fun publish(post: Post): PostResponse<T>

  /**
   * Publish one or more [TimelineData] events to the target social network.
   *
   * @param timelineData A list of [TimelineData] events to publish.
   */
  fun publishTimelineEvents(timelineData: List<TimelineData>) {
    val postTargetName = getPostTarget().displayName(capitalize = false)
    logger.debug { "Executing scheduled publish of timeline posts to target '$postTargetName'..." }
    meterRegistry.counter(PUBLISH_EXECUTIONS, "target", postTargetName).count()

    val posts =
      timelineData.mapNotNull { d ->
        convertEventToPost(timelineData = d, postTarget = getPostTarget())
      }

    if (!CollectionUtils.isEmpty(posts)) {
      Flux
        .fromStream(posts.stream())
        .map(this::publish)
        .doOnError(this::handleError)
        .onErrorComplete()
        .blockLast()
    } else {
      logger.debug { "Did not find any timeline events for date '${dateUtils.today()}'." }
    }
  }

  private fun convertEventToPost(
    timelineData: TimelineData,
    postTarget: PostTarget,
  ): Post? {
    try {
      return postFormatUtils.generatePost(
        timelineData,
        timelineDataLoader.getAdditionalHistoryContext(timelineData),
        postTarget,
      )
    } catch (e: PostException) {
      logger.error(e) { "Unable to generate post for timeline data $timelineData." }
      NewRelic.noticeError(
        e,
        mapOf(
          "timeline_title" to timelineData.title,
          "timeline_description" to timelineData.description,
          "timeline_date" to timelineData.date,
          "timeline_type" to timelineData.type,
          "timeline_year" to timelineData.year,
        ),
      )
      return null
    }
  }

  private fun handleError(throwable: Throwable) {
    logger.error(throwable) { "Unable to publish post." }
    NewRelic.noticeError(throwable)
  }
}

/** Bluesky implementation of the [PostPublisherService]. */
@SuppressFBWarnings(value = ["EI_EXPOSE_REP2", "BC_BAD_CAST_TO_ABSTRACT_COLLECTION"])
class BlueSkyPostPublisherService(
  dateUtils: DateUtils,
  meterRegistry: MeterRegistry,
  postFormatUtils: PostFormatUtils,
  timelineDataLoader: TimelineDataLoader,
  private val blueSkyClient: BlueSkyClient,
) : PostPublisherService<BlueSkyCreateRecordResponse>(
    dateUtils,
    meterRegistry,
    postFormatUtils,
    timelineDataLoader,
  ) {
  override fun getPostTarget(): PostTarget = PostTarget.BLUESKY

  override fun publish(post: Post): PostResponse<BlueSkyCreateRecordResponse> {
    val blueSkyRecord = post.toBlueskyRecord()
    val accessToken = blueSkyClient.createSession().accessJwt
    val response =
      blueSkyClient.createRecord(blueSkyRecord = blueSkyRecord, accessToken = accessToken)

    val rootReplyPost = response.toReplyPost()
    var parentReplyPost: BlueSkyReplyPost? = null

    post.getReplies().forEach { replyText ->
      val reply =
        BlueSkyUtils.createReply(rootMessage = rootReplyPost, parentMessage = parentReplyPost)
      val replyResponse =
        blueSkyClient.createRecord(
          BlueSkyUtils.createRecord(text = replyText, reply = reply),
          accessToken = accessToken,
        )
      parentReplyPost = replyResponse.toReplyPost()
    }

    return createPostResponse(response)
  }
}

/** Twitter implementation of the [PostPublisherService]. */
@SuppressFBWarnings(
  value =
    [
      "EI_EXPOSE_REP2",
      "BC_BAD_CAST_TO_ABSTRACT_COLLECTION",
      "NP_LOAD_OF_KNOWN_NULL_VALUE",
      "SA_LOCAL_SELF_ASSIGNMENT",
    ],
)
class TwitterPostPublisherService(
  dateUtils: DateUtils,
  meterRegistry: MeterRegistry,
  postFormatUtils: PostFormatUtils,
  timelineDataLoader: TimelineDataLoader,
  private val twitterApiUtils: TwitterApiUtils,
) : PostPublisherService<TweetCreateResponse>(
    dateUtils,
    meterRegistry,
    postFormatUtils,
    timelineDataLoader,
  ) {
  override fun getPostTarget(): PostTarget = PostTarget.TWITTER

  override fun publish(post: Post): PostResponse<TweetCreateResponse> {
    val tweetCreateRequest = post.toTweetCreateRequest()

    // Publish the main tweet first
    val response = publishTweet(tweetCreateRequest)

    // If successful, reply to the main tweet with the overflow.
    return if (response != null) {
      val replies: List<TweetCreateRequest> = post.toTweetReplies(response.data!!.id)
      if (CollectionUtils.isEmpty(replies)) {
        createPostResponse(response)
      } else {
        createPostResponse(Flux.fromIterable(replies).map(this::publishTweet).blockLast())
      }
    } else {
      createPostResponse(response)
    }
  }

  private fun publishTweet(tweetCreateRequest: TweetCreateRequest): TweetCreateResponse? =
    try {
      logger.debug { "Twitter API request = $tweetCreateRequest" }
      val tweetResponse =
        twitterApiUtils.twitterApi
          .tweets()
          .createTweet(tweetCreateRequest)
          .execute()
      logger.debug { "Twitter API response = $tweetResponse" }
      meterRegistry
        .counter(
          TIMELINE_EVENTS_PUBLISHED,
          "target",
          getPostTarget().displayName(capitalize = false),
        ).count()
      tweetResponse
    } catch (e: ApiException) {
      logger.error(e) { "Unable to publish tweet $tweetCreateRequest." }
      NewRelic.noticeError(
        e,
        mapOf("today" to dateUtils.today(), "tweet" to tweetCreateRequest.text!!),
      )
      meterRegistry
        .counter(
          TIMELINE_EVENTS_PUBLISHED_FAILURES,
          "target",
          getPostTarget().displayName(capitalize = false),
        ).count()
      null
    }
}
