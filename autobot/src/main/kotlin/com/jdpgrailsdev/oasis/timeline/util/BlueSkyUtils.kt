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
package com.jdpgrailsdev.oasis.timeline.util

import com.jdpgrailsdev.oasis.timeline.client.BlueSkyRecord
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReply
import com.jdpgrailsdev.oasis.timeline.client.BlueSkyReplyPost
import com.jdpgrailsdev.oasis.timeline.data.Post
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Utility singleton that contains various operations related to Bluesky content. */
object BlueSkyUtils {
  private val TIMESTAMP_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").withZone(ZoneId.of("GMT"))

  fun createRecord(
    text: String,
    reply: BlueSkyReply? = null,
  ): BlueSkyRecord = BlueSkyRecord(text = text, createdAt = TIMESTAMP_FORMAT.format(Instant.now()), reply = reply)

  fun createReply(
    rootMessage: BlueSkyReplyPost? = null,
    parentMessage: BlueSkyReplyPost? = null,
  ): BlueSkyReply = BlueSkyReply(root = rootMessage ?: parentMessage, parent = parentMessage ?: rootMessage)
}

fun Post.toBlueskyRecord(): BlueSkyRecord = BlueSkyUtils.createRecord(text = this.getMainPost())
