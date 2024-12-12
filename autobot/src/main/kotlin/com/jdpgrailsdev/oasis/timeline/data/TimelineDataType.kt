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

package com.jdpgrailsdev.oasis.timeline.data

import com.fasterxml.jackson.annotation.JsonProperty

/** Represents the different types of timeline data events. */
enum class TimelineDataType(
  private val codePoint: Int,
  private val surrogatePair: String,
) {
  // star emoji
  @JsonProperty("certifications")
  CERTIFICATIONS(codePoint = 0x2B50, surrogatePair = "\u2B50"),

  // microphone emoji
  @JsonProperty("gigs")
  GIGS(codePoint = 0x1F3A4, surrogatePair = "\uD83C\uDFA4"),

  // newspaper emoji
  @JsonProperty("noteworthy")
  NOTEWORTHY(codePoint = 0x1F4F0, surrogatePair = "\uD83D\uDCF0"),

  // camera with flash emoji
  @JsonProperty("photo")
  PHOTO(codePoint = 0x1F4F8, surrogatePair = "\uD83D\uDCF8"),

  // control knobs emoji
  @JsonProperty("recordings")
  RECORDINGS(codePoint = 0x1F39B, "\uD83C\uDF9B"),

  // music note emoji
  @JsonProperty("releases")
  RELEASES(codePoint = 0x1F3B5, "\uD83C\uDFB5"),

  // movie camera emoji
  @JsonProperty("videos")
  VIDEOS(0x1F3A5, "\uD83C\uDFA5"),
  ;

  fun getEmoji(supportsUnicode21: Boolean): String = if (supportsUnicode21) String(Character.toChars(codePoint)) else surrogatePair

  override fun toString(): String = name.lowercase()
}
