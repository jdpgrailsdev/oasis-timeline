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

package com.jdpgrailsdev.oasis.timeline.data.model.twitter;

import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEvent;
import com.jdpgrailsdev.oasis.timeline.data.model.PublishedEventException;
import twitter4j.v1.GeoLocation;
import twitter4j.v1.StatusUpdate;

/** Represents a Twitter tweet message. */
public class Tweet extends PublishedEvent<StatusUpdate> {

  public static final GeoLocation LOCATION = GeoLocation.of(53.422201, -2.208914);

  public static final Integer TWEET_LIMIT = 280;

  /**
   * Creates a new published event.
   *
   * @param text The text of the published event.
   * @throws PublishedEventException if the provided text is blank.
   */
  public Tweet(final String text) throws PublishedEventException {
    super(text, TWEET_LIMIT);
  }

  @Override
  protected StatusUpdate createEvent(final String text, final Long inReplyToStatusId) {
    final StatusUpdate update =
        StatusUpdate.of(text.trim())
            .displayCoordinates(true)
            .location(LOCATION.latitude, LOCATION.longitude);
    if (inReplyToStatusId != null) {
      return update.inReplyToStatusId(inReplyToStatusId);
    } else {
      return update;
    }
  }
}
