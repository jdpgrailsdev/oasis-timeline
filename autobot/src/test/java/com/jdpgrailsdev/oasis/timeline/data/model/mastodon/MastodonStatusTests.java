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

package com.jdpgrailsdev.oasis.timeline.data.model.mastodon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.io.Resources;
import com.jdpgrailsdev.oasis.timeline.AssertionMessage;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEvent;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests suite for the {@link MastodonStatus} class. */
class MastodonStatusTests {

  @Test
  @DisplayName("test that when a status is created for a blank status, an exception is raised")
  void testExceptionForBlankStatus() {
    Assertions.assertThrows(PublishedEventException.class, () -> new MastodonStatus(null));

    Assertions.assertThrows(PublishedEventException.class, () -> new MastodonStatus(""));
  }

  @Test
  @DisplayName(
      "test that when the main message is retrieved, the first message in the underlying collection"
          + " is returned")
  void testFirstMessageRetrieved() throws PublishedEventException, IOException {
    final String text =
        Resources.toString(Resources.getResource("text/mastodon_long.txt"), StandardCharsets.UTF_8);
    final MastodonStatus status = new MastodonStatus(text);
    final MastodonStatusUpdate mainMessage = status.getMainMessage();

    assertEquals(
        text.substring(0, 487) + PublishedEvent.ELIPSES,
        mainMessage.status(),
        AssertionMessage.VALUE.toString());

    final List<String> messages = status.getMessages();
    assertEquals(2, messages.size(), AssertionMessage.SIZE.toString());
  }

  @Test
  @DisplayName(
      "test that when an event that exceeds the limit of characters is appropriately broken up into"
          + " individual parts")
  void testSplittingLongMessage() throws PublishedEventException, IOException {
    final String text =
        Resources.toString(
            Resources.getResource("text/mastodon_multi_parts.txt"), StandardCharsets.UTF_8);
    final MastodonStatus status = new MastodonStatus(text);

    assertEquals(4, status.getMessages().size(), AssertionMessage.SIZE.toString());
    assertTrue(
        status.getMessages().get(0).length() <= MastodonStatus.STATUS_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        text.substring(0, 432) + PublishedEvent.ELIPSES,
        status.getMessages().get(0),
        AssertionMessage.VALUE.toString());
    assertTrue(
        status.getMessages().get(1).length() <= MastodonStatus.STATUS_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        PublishedEvent.ELIPSES
            + PublishedEvent.SPACE
            + text.substring(433, 863)
            + PublishedEvent.ELIPSES,
        status.getMessages().get(1),
        AssertionMessage.VALUE.toString());
    assertTrue(
        status.getMessages().get(2).length() <= MastodonStatus.STATUS_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        PublishedEvent.ELIPSES
            + PublishedEvent.SPACE
            + text.substring(864, 1294)
            + PublishedEvent.ELIPSES,
        status.getMessages().get(2),
        AssertionMessage.VALUE.toString());
    assertTrue(
        status.getMessages().get(3).length() <= MastodonStatus.STATUS_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        PublishedEvent.ELIPSES + PublishedEvent.SPACE + text.substring(1295),
        status.getMessages().get(3),
        AssertionMessage.VALUE.toString());
  }

  @Test
  @DisplayName(
      "test that when an event exceeds the limit but the split part ends a sentence, the message is"
          + " appropriately broken up into individual parts without elipses")
  void testSplitMessageSentenceEnd() throws PublishedEventException, IOException {
    final String text =
        Resources.toString(
            Resources.getResource("text/mastodon_long_end_of_sentence.txt"),
            StandardCharsets.UTF_8);
    final MastodonStatus status = new MastodonStatus(text);

    assertEquals(2, status.getMessages().size(), AssertionMessage.SIZE.toString());
    assertTrue(
        status.getMessages().get(0).length() <= MastodonStatus.STATUS_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        text.substring(0, 488), status.getMessages().get(0), AssertionMessage.VALUE.toString());
    assertTrue(
        status.getMessages().get(1).length() <= MastodonStatus.STATUS_LIMIT,
        AssertionMessage.LENGTH.toString());
    assertEquals(
        text.substring(489), status.getMessages().get(1), AssertionMessage.VALUE.toString());
  }
}
