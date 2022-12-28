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

package com.jdpgrailsdev.oasis.timeline.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

/**
 * Generic representation of a published event. A published event represents a timeline event that
 * has been converted so that it may be sent to a social platform.
 *
 * @param <T> The type used to invoke a social platform API in order to publish the event.
 */
public abstract class PublishedEvent<T> {

  public static final String ELIPSES = "...";

  public static final String SPACE = " ";

  /** Accounts for the emoji and elipses added to multi-part messages. */
  private static final Integer ADDITIONAL_CHARACTERS = ELIPSES.length() + 1;

  private static final Double REDUCTION_PERCENTAGE = 0.85;

  private final List<String> messages;

  /**
   * Creates a new published event.
   *
   * @param text The text of the published event.
   * @param limit The character limit for the published event
   * @throws PublishedEventException if the provided text is blank.
   */
  public PublishedEvent(final String text, final Integer limit) throws PublishedEventException {
    if (StringUtils.hasText(text)) {
      if (text.length() > limit) {
        messages = splitMessage(text, limit);
      } else {
        messages = Lists.newArrayList(text);
      }
    } else {
      throw new PublishedEventException("Published event message may not be blank.");
    }
  }

  /**
   * Retrieves the messages associated with this published event. Published events may have multiple
   * parts.
   *
   * @return The message parts associated with the published event.
   */
  public List<String> getMessages() {
    return ImmutableList.copyOf(messages);
  }

  /**
   * Retrieves the main/first message in a status update.
   *
   * @return The main message of the status update or an empty string if no messages are available.
   */
  @JsonIgnore
  public T getMainMessage() {
    return createEvent(getMessages().stream().findFirst().orElse(""), null);
  }

  /**
   * Returns the replies for the main event if the message exceeds the API limit.
   *
   * @param inReplyToId The ID of the main event.
   * @return The replies to the main event or an empty list if no replies are necessary.
   */
  @JsonIgnore
  public List<T> getReplies(final Long inReplyToId) {
    return getMessages().stream()
        .skip(1)
        .map(r -> createEvent(r, inReplyToId))
        .collect(Collectors.toList());
  }

  /**
   * Creates an event to be published to a social platform.
   *
   * @param text The text of the event.
   * @param inReplyToStatusId The ID of the message that this message may be in reply to. This may
   *     be {@code null}.
   * @return The generated event suitable for publishing to a social platform API.
   */
  protected abstract T createEvent(String text, Long inReplyToStatusId);

  /**
   * Splits the message text in accordance with the limit of a social platform.
   *
   * @param text The text to be possibly split.
   * @param limit The limit (of characters) in the message.
   * @return The list of message parts split in accordance with the limit of the target platform.
   */
  private List<String> splitMessage(final String text, final Integer limit) {
    final List<String> events = Lists.newArrayList();
    int size = text.length();

    while (size >= (limit - ADDITIONAL_CHARACTERS)) {
      size = (int) Math.floor(size * REDUCTION_PERCENTAGE);
    }

    final StringBuilder builder = new StringBuilder();
    final List<String> words = Splitter.on(' ').splitToList(text);
    for (final String word : words) {
      if ((builder.length() + word.length()) <= size) {
        builder.append(SPACE);
      } else {
        final boolean useEllipses = !builder.toString().trim().endsWith(".");
        if (useEllipses) {
          builder.append(ELIPSES);
        }
        events.add(builder.toString().trim());
        builder.setLength(0);
        if (useEllipses) {
          builder.append(ELIPSES);
          builder.append(SPACE);
        }
      }
      builder.append(word);
    }
    events.add(builder.toString().trim());
    return events;
  }
}
