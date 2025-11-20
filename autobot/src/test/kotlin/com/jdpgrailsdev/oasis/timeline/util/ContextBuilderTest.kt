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

import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class ContextBuilderTest {
  @ParameterizedTest
  @EnumSource(PostTarget::class)
  @DisplayName("test that when all fields are set on the builder, the context can be built")
  fun testContextBuildingAllFields(postTarget: PostTarget?) {
    val additionalContext = "additional context"
    val description = "description"
    val hashtags = "#hashtags"
    val mentions = "@mentions"
    val type = TimelineDataType.CERTIFICATIONS
    val year = 2021

    val context =
      ContextBuilder()
        .withAdditionalContext(additionalContext)
        .withDescription(description)
        .withHashtags(hashtags)
        .withMentions(mentions)
        .withSupportsUnicode21(postTarget == PostTarget.TWITTER)
        .withType(type)
        .withYear(year)
        .build()

    Assertions.assertNotNull(context)
    Assertions.assertEquals(additionalContext, context!!.getVariable("additionalContext"))
    Assertions.assertEquals(description, context.getVariable("description"))
    Assertions.assertEquals(
      type.getEmoji(postTarget == PostTarget.TWITTER),
      context.getVariable("emoji"),
    )
    Assertions.assertEquals(hashtags, context.getVariable("hashtags"))
    Assertions.assertEquals(mentions, context.getVariable("mentions"))
    Assertions.assertEquals(type.toString(), context.getVariable("type"))
    Assertions.assertEquals(year, context.getVariable("year"))
  }

  @Test
  @DisplayName("test that when the type field is not set, the context can not be built")
  fun testMissingRequiredFields() {
    val additionalContext = "additional context"
    val description = "description"
    val hashtags = "#hashtags"
    val mentions = "@mentions"
    val year = 2021

    Assertions.assertThrows(NullPointerException::class.java) {
      ContextBuilder()
        .withAdditionalContext(additionalContext)
        .withDescription(description)
        .withHashtags(hashtags)
        .withMentions(mentions)
        .withYear(year)
        .build()
    }
  }
}
