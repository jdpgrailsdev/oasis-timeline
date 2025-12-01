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

package com.jdpgrailsdev.oasis.timeline.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.http.MediaType
import java.io.IOException

const val BLUE_SKY_CREATE_SESSION_URI = "/xrpc/com.atproto.server.createSession"
const val BLUE_SKY_CREATE_RECORD_URI = "/xrpc/com.atproto.repo.createRecord"
const val BLUE_SKY_SEARCH_POSTS_URI = "/xrpc/app.bsky.feed.searchPosts"
const val BLUE_SKY_GET_PROFILE_URI = "/xrpc/app.bsky.actor.getProfile"

/** Client that wraps various Bluesky REST API operations. */
@SuppressFBWarnings(value = ["EI_EXPOSE_REP", "EI_EXPOSE_REP2"])
class BlueSkyClient(
  private val blueSkyUrl: String,
  private val publicBlueSkyUrl: String,
  private val blueSkyHandle: String,
  private val blueSkyPassword: String,
  private val client: OkHttpClient,
  private val mapper: ObjectMapper,
) {
  /**
   * Invokes the Bluesky REST API createSession endpoint to start an authenticated session.
   *
   * @return The [BlueSkyCreateSessionResponse] containing the authentication tokens.
   * @throws IOException if unable to successfully execute the API call.
   */
  fun createSession(): BlueSkyCreateSessionResponse {
    val body =
      mapper
        .writeValueAsString(mapOf("identifier" to blueSkyHandle, "password" to blueSkyPassword))
        .toRequestBody("application/json; charset=utf-8".toMediaType())
    val request =
      Request
        .Builder()
        .url("$blueSkyUrl$BLUE_SKY_CREATE_SESSION_URI")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .post(body)
        .build()
    client.newCall(request).execute().use { response ->
      if (response.isSuccessful) {
        return mapper.readValue(response.body.string(), BlueSkyCreateSessionResponse::class.java)
      } else {
        throw IOException("Unexpected response $response.  Body = ${response.body.string()}")
      }
    }
  }

  /**
   * Invokes the Bluesky REST API createRecord endpoint to post a new social post to the
   * authenticated account's feed.
   *
   * @param blueSkyRecord The record to be posted to the feed
   * @param accessToken The authentication access token
   * @return The [BlueSkyCreateSessionResponse] containing information used for additional post
   *   replies.
   * @throws IOException if unable to successfully post the record.
   */
  @SuppressFBWarnings("SA_LOCAL_SELF_ASSIGNMENT")
  fun createRecord(
    blueSkyRecord: BlueSkyRecord,
    accessToken: String,
  ): BlueSkyCreateRecordResponse {
    val body =
      mapper
        .writeValueAsBytes(BlueSkyCreateRecordRequest(repo = blueSkyHandle, record = blueSkyRecord))
        .toString(Charsets.UTF_8)
        .toRequestBody(MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val request =
      Request
        .Builder()
        .url("$blueSkyUrl$BLUE_SKY_CREATE_RECORD_URI")
        .header("Authorization", "BEARER $accessToken")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .post(body)
        .build()
    return client.newCall(request).execute().use { response ->
      if (response.isSuccessful) {
        mapper.readValue(response.body.string(), BlueSkyCreateRecordResponse::class.java)
      } else {
        throw IOException("Unexpected response $response.  Body = ${response.body.string()}")
      }
    }
  }

  /**
   * Fetches the profile associated with the provided BlueSky handle.
   *
   * @param handle A BlueSky handle
   * @return The [BlueSkyProfileResponse] containing the profile information.
   * @throws IOException if unable to successfully get the profile.
   */
  fun getProfile(handle: String): BlueSkyProfileResponse {
    val request =
      Request
        .Builder()
        .url(
          "$publicBlueSkyUrl$BLUE_SKY_GET_PROFILE_URI"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("actor", handle)
            .build(),
        ).build()
    return client.newCall(request).execute().use { response ->
      if (response.isSuccessful) {
        mapper.readValue(response.body.string(), BlueSkyProfileResponse::class.java)
      } else {
        throw IOException("Unexpected response $response.  Body = ${response.body.string()}")
      }
    }
  }

  @SuppressFBWarnings(value = ["BC_BAD_CAST_TO_ABSTRACT_COLLECTION", "SA_LOCAL_SELF_ASSIGNMENT"])
  fun getPosts(): List<String> {
    val httpUrl =
      "$publicBlueSkyUrl$BLUE_SKY_SEARCH_POSTS_URI?author=$blueSkyHandle&q=OnThisDay".toHttpUrl()
    val request =
      Request
        .Builder()
        .url(httpUrl)
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .build()
    return client.newCall(request).execute().use { response ->
      if (response.isSuccessful) {
        mapper.readValue(response.body.string(), BlueSkyPostSearchResponse::class.java).posts.map {
          it.record.text
        }
      } else {
        throw IOException("Unexpected response $response.  Body = ${response.body.string()}")
      }
    }
  }
}

data class BlueSkyReplyPost(
  val uri: String,
  val cid: String,
)

data class BlueSkyReply(
  val root: BlueSkyReplyPost? = null,
  val parent: BlueSkyReplyPost? = null,
)

@SuppressFBWarnings(value = ["EI_EXPOSE_REP", "EI_EXPOSE_REP2"])
@JsonInclude(JsonInclude.Include.NON_NULL)
data class BlueSkyRecord(
  val text: String,
  val createdAt: String,
  val facets: List<BlueSkyFacet> = emptyList(),
  val reply: BlueSkyReply? = null,
)

data class BlueSkyCreateRecordRequest(
  val repo: String,
  val record: BlueSkyRecord,
  val collection: String = "app.bsky.feed.post",
)

data class BlueSkyFacetIndex(
  val byteStart: Int,
  val byteEnd: Int,
)

@SuppressFBWarnings(value = ["EI_EXPOSE_REP", "EI_EXPOSE_REP2"])
data class BlueSkyFacet(
  val index: BlueSkyFacetIndex,
  val features: List<BlueSkyFacetFeature>,
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "\$type")
@JsonSubTypes(
  JsonSubTypes.Type(
    value = BlueSkyMentionFacetFeature::class,
    name = "app.bsky.richtext.facet#mention",
  ),
  JsonSubTypes.Type(value = BlueSkyTagFacetFeature::class, name = "app.bsky.richtext.facet#tag"),
)
abstract class BlueSkyFacetFeature

data class BlueSkyMentionFacetFeature(
  val did: String,
) : BlueSkyFacetFeature()

data class BlueSkyTagFacetFeature(
  val tag: String,
) : BlueSkyFacetFeature()

enum class BlueSkyFacetType {
  MENTION,
  TAG,
}

data class BlueSkyCreateRecordResponse(
  val uri: String,
  val cid: String,
  val commit: BlueSkyRecordCommit,
  val validationStatus: String = "unknown",
)

data class BlueSkyRecordCommit(
  val cid: String,
  val rev: String,
)

@SuppressFBWarnings(value = ["EI_EXPOSE_REP", "EI_EXPOSE_REP2"])
data class BlueSkyPostSearchResponse(
  val posts: List<BlueSkyPost>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BlueSkyPost(
  val record: BlueSkyRecord,
)

@SuppressFBWarnings(value = ["EI_EXPOSE_REP", "EI_EXPOSE_REP2"])
data class BlueSkyCreateSessionResponse(
  val accessJwt: String,
  val refreshJwt: String,
  val handle: String,
  val did: String,
  val didDoc: Map<String, Any> = emptyMap(),
  val email: String = "",
  val emailConfirmed: Boolean = false,
  val emailAuthFactor: Boolean = false,
  val active: Boolean = false,
  val status: String? = null,
)

data class BlueSkyCredentials(
  val accessToken: String,
  val refreshToken: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BlueSkyProfileResponse(
  val did: String,
  val handle: String,
)

fun BlueSkyCreateRecordResponse.toReplyPost(): BlueSkyReplyPost = BlueSkyReplyPost(uri = this.uri, cid = this.cid)

fun BlueSkyCreateSessionResponse.toBlueSkyCredentials(): BlueSkyCredentials =
  BlueSkyCredentials(accessToken = this.accessJwt, refreshToken = this.refreshJwt)
