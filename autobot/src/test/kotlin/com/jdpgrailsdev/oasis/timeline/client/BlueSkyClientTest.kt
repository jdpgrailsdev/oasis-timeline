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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jdpgrailsdev.oasis.timeline.data.Post
import com.jdpgrailsdev.oasis.timeline.util.toBlueskyRecord
import io.mockk.every
import io.mockk.mockk
import okhttp3.Call
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.io.IOException

internal class BlueSkyClientTest {
  private lateinit var mapper: ObjectMapper

  @BeforeEach
  fun setUp() {
    mapper = ObjectMapper()
    mapper.registerModule(KotlinModule.Builder().build())
  }

  @Test
  fun testCreateSession() {
    val expectedToken = "token"
    val url = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val responseBody =
      mapper
        .writeValueAsString(
          BlueSkyCreateSessionResponse(
            accessJwt = expectedToken,
            refreshJwt = "refresh",
            handle = handle,
            did = "did",
          ),
        ).toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> {
            every { execute() } returns
              Response
                .Builder()
                .message("response")
                .protocol(Protocol.HTTP_1_1)
                .request(mockk<Request>())
                .body(responseBody)
                .code(HttpStatus.OK.value())
                .build()
          }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = url,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    val response = client.createSession()
    assertEquals(expectedToken, response.accessJwt)
  }

  @Test
  fun testCreateSessionFailure() {
    val actualUrl = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val request = mockk<Request> { every { url } returns actualUrl.toHttpUrl() }
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> {
            every { execute() } returns
              Response
                .Builder()
                .message("response")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .code(HttpStatus.NOT_FOUND.value())
                .build()
          }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = actualUrl,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    assertThrows(IOException::class.java) { client.createSession() }
  }

  @Test
  fun testCreateRecord() {
    val token = "token"
    val url = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val cid = "bafyreig2fjxi3rptqdgylg7e5hmjl6mcke7rn2b6cugzlqq3i4zu6rq52q"
    val uri = "at://did:plc:u5cwb2mwiv2bfq53cjufe6yn/app.bsky.feed.post/3k43tv4rft22g"
    val createPostResponseBody =
      mapper
        .writeValueAsString(
          BlueSkyCreateRecordResponse(
            uri = uri,
            cid = cid,
            commit = BlueSkyRecordCommit(cid = cid, rev = "1"),
          ),
        ).toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val createPostResponse =
      Response
        .Builder()
        .body(createPostResponseBody)
        .message("postResponse")
        .protocol(Protocol.HTTP_1_1)
        .request(mockk<Request>())
        .code(HttpStatus.OK.value())
        .build()
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> { every { execute() } returns createPostResponse }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = url,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    val post = Post(text = "a message", limit = 140)
    val response = client.createRecord(blueSkyRecord = post.toBlueskyRecord(), accessToken = token)
    assertEquals(cid, response.cid)
    assertEquals(uri, response.uri)
  }

  @Test
  fun testCreateRecordWithFacets() {
    val token = "token"
    val url = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val cid = "bafyreig2fjxi3rptqdgylg7e5hmjl6mcke7rn2b6cugzlqq3i4zu6rq52q"
    val uri = "at://did:plc:u5cwb2mwiv2bfq53cjufe6yn/app.bsky.feed.post/3k43tv4rft22g"
    val profileDid = "did:plc:2kfn5kwq4dqzncgv2g2tqmii"
    val profileHandle = "user.bksy.social"
    val getProfileResponseBody =
      mapper
        .writeValueAsString(BlueSkyProfileResponse(did = profileDid, handle = profileHandle))
        .toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val getProfileResponse =
      Response
        .Builder()
        .body(getProfileResponseBody)
        .message("profileResponse")
        .protocol(Protocol.HTTP_1_1)
        .request(mockk<Request>())
        .code(HttpStatus.OK.value())
        .build()
    val createPostResponseBody =
      mapper
        .writeValueAsString(
          BlueSkyCreateRecordResponse(
            uri = uri,
            cid = cid,
            commit = BlueSkyRecordCommit(cid = cid, rev = "1"),
          ),
        ).toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val createPostResponse =
      Response
        .Builder()
        .body(createPostResponseBody)
        .message("postResponse")
        .protocol(Protocol.HTTP_1_1)
        .request(mockk<Request>())
        .code(HttpStatus.OK.value())
        .build()
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> {
            every { execute() } returnsMany listOf(getProfileResponse, createPostResponse)
          }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = url,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    val post = Post(text = "a message with @$profileHandle", limit = 140)
    val response = client.createRecord(blueSkyRecord = post.toBlueskyRecord(), accessToken = token)
    assertEquals(cid, response.cid)
    assertEquals(uri, response.uri)
  }

  @Test
  fun testCreateRecordWithRoot() {
    val token = "token"
    val url = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val cid = "bafyreig2fjxi3rptqdgylg7e5hmjl6mcke7rn2b6cugzlqq3i4zu6rq52q"
    val uri = "at://did:plc:u5cwb2mwiv2bfq53cjufe6yn/app.bsky.feed.post/3k43tv4rft22g"
    val rootMessage =
      BlueSkyCreateRecordResponse(
        uri = uri,
        cid = cid,
        commit = BlueSkyRecordCommit(cid = cid, rev = "1"),
      )
    val createPostResponseBody =
      mapper
        .writeValueAsString(rootMessage)
        .toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val createPostResponse =
      Response
        .Builder()
        .body(createPostResponseBody)
        .message("postResponse")
        .protocol(Protocol.HTTP_1_1)
        .request(mockk<Request>())
        .code(HttpStatus.OK.value())
        .build()
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> { every { execute() } returns createPostResponse }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = url,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    val record =
      BlueSkyRecord(
        text = "a message",
        createdAt = "2023-08-07T05:49:40.501974Z",
        reply = BlueSkyReply(root = rootMessage.toReplyPost()),
      )
    val response = client.createRecord(blueSkyRecord = record, accessToken = token)
    assertEquals(cid, response.cid)
    assertEquals(uri, response.uri)
  }

  @Test
  fun testCreateRecordWithRootAndParent() {
    val token = "token"
    val url = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val cid = "bafyreig2fjxi3rptqdgylg7e5hmjl6mcke7rn2b6cugzlqq3i4zu6rq52q"
    val uri = "at://did:plc:u5cwb2mwiv2bfq53cjufe6yn/app.bsky.feed.post/3k43tv4rft22g"
    val rootMessage =
      BlueSkyCreateRecordResponse(
        uri = uri,
        cid = cid,
        commit = BlueSkyRecordCommit(cid = cid, rev = "1"),
      )
    val parentMessage =
      BlueSkyCreateRecordResponse(
        uri = uri,
        cid = cid,
        commit = BlueSkyRecordCommit(cid = cid, rev = "1"),
      )
    val createPostResponseBody =
      mapper
        .writeValueAsString(rootMessage)
        .toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val createPostResponse =
      Response
        .Builder()
        .body(createPostResponseBody)
        .message("postResponse")
        .protocol(Protocol.HTTP_1_1)
        .request(mockk<Request>())
        .code(HttpStatus.OK.value())
        .build()
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> { every { execute() } returns createPostResponse }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = url,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    val record =
      BlueSkyRecord(
        text = "a message",
        createdAt = "2023-08-07T05:49:40.501974Z",
        reply = BlueSkyReply(root = rootMessage.toReplyPost(), parent = parentMessage.toReplyPost()),
      )
    val response = client.createRecord(blueSkyRecord = record, accessToken = token)
    assertEquals(cid, response.cid)
    assertEquals(uri, response.uri)
  }

  @Test
  fun testCreateRecordWithParent() {
    val token = "token"
    val url = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val cid = "bafyreig2fjxi3rptqdgylg7e5hmjl6mcke7rn2b6cugzlqq3i4zu6rq52q"
    val uri = "at://did:plc:u5cwb2mwiv2bfq53cjufe6yn/app.bsky.feed.post/3k43tv4rft22g"
    val parentMessage =
      BlueSkyCreateRecordResponse(
        uri = uri,
        cid = cid,
        commit = BlueSkyRecordCommit(cid = cid, rev = "1"),
      )
    val createPostResponseBody =
      mapper
        .writeValueAsString(parentMessage)
        .toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val createPostResponse =
      Response
        .Builder()
        .body(createPostResponseBody)
        .message("postResponse")
        .protocol(Protocol.HTTP_1_1)
        .request(mockk<Request>())
        .code(HttpStatus.OK.value())
        .build()
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> { every { execute() } returns createPostResponse }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = url,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    val record =
      BlueSkyRecord(
        text = "a message",
        createdAt = "2023-08-07T05:49:40.501974Z",
        reply = BlueSkyReply(parent = parentMessage.toReplyPost()),
      )
    val response = client.createRecord(blueSkyRecord = record, accessToken = token)
    assertEquals(cid, response.cid)
    assertEquals(uri, response.uri)
  }

  @Test
  fun testCreateRecordFailure() {
    val token = "token"
    val actualUrl = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val cid = "bafyreig2fjxi3rptqdgylg7e5hmjl6mcke7rn2b6cugzlqq3i4zu6rq52q"
    val uri = "at://did:plc:u5cwb2mwiv2bfq53cjufe6yn/app.bsky.feed.post/3k43tv4rft22g"
    val request = mockk<Request> { every { url } returns actualUrl.toHttpUrl() }
    val rootMessage =
      BlueSkyCreateRecordResponse(
        uri = uri,
        cid = cid,
        commit = BlueSkyRecordCommit(cid = cid, rev = "1"),
      )
    val createPostResponseBody =
      mapper
        .writeValueAsString(rootMessage)
        .toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val createPostResponse =
      Response
        .Builder()
        .body(createPostResponseBody)
        .message("postResponse")
        .protocol(Protocol.HTTP_1_1)
        .request(request)
        .code(HttpStatus.NOT_FOUND.value())
        .build()
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> { every { execute() } returns createPostResponse }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = actualUrl,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )
    val record =
      BlueSkyRecord(
        text = "a message",
        createdAt = "2023-08-07T05:49:40.501974Z",
        reply = BlueSkyReply(root = rootMessage.toReplyPost(), parent = rootMessage.toReplyPost()),
      )
    assertThrows(IOException::class.java) {
      client.createRecord(blueSkyRecord = record, accessToken = token)
    }
  }

  @Test
  fun testConversionToReplyFromResponse() {
    val cid = "bafyreig2fjxi3rptqdgylg7e5hmjl6mcke7rn2b6cugzlqq3i4zu6rq52q"
    val uri = "at://did:plc:u5cwb2mwiv2bfq53cjufe6yn/app.bsky.feed.post/3k43tv4rft22g"
    val response =
      BlueSkyCreateRecordResponse(
        uri = uri,
        cid = cid,
        commit = BlueSkyRecordCommit(cid = cid, rev = "1"),
      )
    val reply = response.toReplyPost()
    assertEquals(cid, reply.cid)
    assertEquals(uri, reply.uri)
  }

  @Test
  fun testConversionToCredentialsFromResponse() {
    val accessToken = "token"
    val refreshToken = "refreshToken"
    val handle = "handle"
    val response =
      BlueSkyCreateSessionResponse(
        accessJwt = accessToken,
        refreshJwt = refreshToken,
        handle = handle,
        did = "did",
      )
    val credentials = response.toBlueSkyCredentials()
    assertEquals(accessToken, credentials.accessToken)
    assertEquals(refreshToken, credentials.refreshToken)
  }

  @Test
  fun testGetFeed() {
    val accessToken = "access-token"
    val url = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val feed =
      BlueSkyFeed(
        uri = "uri",
        cid = "cid",
        did = "did",
        displayName = "displayName",
        creator = BlueSkyCreator(did = "did", handle = handle),
        description = "a post description",
      )
    val responseBody =
      mapper
        .writeValueAsString(BlueSkyGetFeedsResponse(cursor = "cursor", feeds = listOf(feed)))
        .toResponseBody(contentType = MediaType.APPLICATION_JSON_VALUE.toMediaType())
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> {
            every { execute() } returns
              Response
                .Builder()
                .message("response")
                .protocol(Protocol.HTTP_1_1)
                .request(mockk<Request>())
                .body(responseBody)
                .code(HttpStatus.OK.value())
                .build()
          }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = url,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    val posts = client.getPosts(accessToken = accessToken)
    assertEquals(1, posts.size)
    assertEquals(feed.description, posts.first())
  }

  @Test
  fun testGetFeedFailureThrowsException() {
    val accessToken = "access-token"
    val actualUrl = "http://localhost:8080"
    val publicUrl = "http://localhost:8080/public"
    val handle = "user"
    val password = "password"
    val request = mockk<Request> { every { url } returns actualUrl.toHttpUrl() }
    val okHttpClient =
      mockk<OkHttpClient> {
        every { newCall(any()) } returns
          mockk<Call> {
            every { execute() } returns
              Response
                .Builder()
                .message("response")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .code(HttpStatus.NOT_FOUND.value())
                .build()
          }
      }

    val client =
      BlueSkyClient(
        blueSkyUrl = actualUrl,
        blueSkyHandle = handle,
        blueSkyPassword = password,
        client = okHttpClient,
        mapper = mapper,
        publicBlueSkyUrl = publicUrl,
      )

    assertThrows(IOException::class.java) { client.getPosts(accessToken = accessToken) }
  }
}
