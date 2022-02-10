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

import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

class ContextBuilderTests {

  @Test
  @DisplayName("test that when all fields are set on the builder, the context can be built")
  void testContextBuildingAllFields() {
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
            .withType(type)
            .withYear(year)
            .build();

    assertNotNull(context, AssertionMessage.NON_NULL.toString());
    assertEquals(
        additionalContext,
        context.getVariable("additionalContext"),
        AssertionMessage.VALUE.toString());
    assertEquals(
        description, context.getVariable("description"), AssertionMessage.VALUE.toString());
    assertEquals(type.getEmoji(), context.getVariable("emoji"), AssertionMessage.VALUE.toString());
    assertEquals(hashtags, context.getVariable("hashtags"), AssertionMessage.VALUE.toString());
    assertEquals(mentions, context.getVariable("mentions"), AssertionMessage.VALUE.toString());
    assertEquals(type.toString(), context.getVariable("type"), AssertionMessage.VALUE.toString());
    assertEquals(year, context.getVariable("year"), AssertionMessage.VALUE.toString());
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
