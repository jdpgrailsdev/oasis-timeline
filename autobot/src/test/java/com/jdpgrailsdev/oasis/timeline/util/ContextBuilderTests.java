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

package com.jdpgrailsdev.oasis.timeline.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.jdpgrailsdev.oasis.timeline.data.PostTarget;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.thymeleaf.context.Context;

class ContextBuilderTests {

  @ParameterizedTest
  @EnumSource(PostTarget.class)
  @DisplayName("test that when all fields are set on the builder, the context can be built")
  void testContextBuildingAllFields(final PostTarget postTarget) {
    final String additionalContext = "additional context";
    final String description = "description";
    final String hashtags = "#hashtags";
    final String mentions = "@mentions";
    final TimelineDataType type = TimelineDataType.CERTIFICATIONS;
    final Integer year = 2021;

    final Context context =
        new ContextBuilder()
            .withAdditionalContext(additionalContext)
            .withDescription(description)
            .withHashtags(hashtags)
            .withMentions(mentions)
            .withSupportsUnicode21(postTarget == PostTarget.TWITTER)
            .withType(type)
            .withYear(year)
            .build();

    assertNotNull(context);
    assertEquals(additionalContext, context.getVariable("additionalContext"));
    assertEquals(description, context.getVariable("description"));
    assertEquals(type.getEmoji(postTarget == PostTarget.TWITTER), context.getVariable("emoji"));
    assertEquals(hashtags, context.getVariable("hashtags"));
    assertEquals(mentions, context.getVariable("mentions"));
    assertEquals(type.toString(), context.getVariable("type"));
    assertEquals(year, context.getVariable("year"));
  }

  @Test
  @DisplayName("test that when the type field is not set, the context can not be built")
  void testMissingRequiredFields() {
    final String additionalContext = "additional context";
    final String description = "description";
    final String hashtags = "#hashtags";
    final String mentions = "@mentions";
    final Integer year = 2021;

    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            new ContextBuilder()
                .withAdditionalContext(additionalContext)
                .withDescription(description)
                .withHashtags(hashtags)
                .withMentions(mentions)
                .withYear(year)
                .build());
  }
}
