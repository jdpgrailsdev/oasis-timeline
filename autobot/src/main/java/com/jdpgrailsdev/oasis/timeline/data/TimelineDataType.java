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

package com.jdpgrailsdev.oasis.timeline.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Locale;

/** Represents the different types of timeline data events. */
public enum TimelineDataType {
  @JsonProperty("certifications")
  CERTIFICATIONS(Character.toChars(0x2B50)), // star emoji
  @JsonProperty("gigs")
  GIGS(Character.toChars(0x1F3A4)), // microphone emoji
  @JsonProperty("noteworthy")
  NOTEWORTHY(Character.toChars(0x1F4F0)), // newspaper emoji
  @JsonProperty("photo")
  PHOTO(Character.toChars(0x1F4F8)), // camera with flash emoji
  @JsonProperty("recordings")
  RECORDINGS(Character.toChars(0x1F39B)), // control knobs emoji
  @JsonProperty("releases")
  RELEASES(Character.toChars(0x1F3B5)), // music note emoji
  @JsonProperty("videos")
  VIDEOS(Character.toChars(0x1F3A5)); // movie camera emoji

  private final char[] unicode;

  TimelineDataType(final char... unicode) {
    this.unicode = new char[unicode.length];
    System.arraycopy(unicode, 0, this.unicode, 0, unicode.length);
  }

  public String getEmoji() {
    return new String(unicode);
  }

  @Override
  public String toString() {
    return name().toLowerCase(Locale.ROOT);
  }
}
