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

import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.thymeleaf.context.Context

/** Builds Thymeleaf contexts. */
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
class ContextBuilder {
  private var additionalContext: String? = null

  private var description: String? = null

  private var hashtags: String? = null

  private var mentions: String? = null

  private var supportsUnicode21 = false

  private var type: TimelineDataType? = null

  private var year: Int? = null

  fun withAdditionalContext(additionalContext: String?): ContextBuilder {
    this.additionalContext = additionalContext
    return this
  }

  fun withDescription(description: String?): ContextBuilder {
    this.description = description
    return this
  }

  fun withHashtags(hashtags: String?): ContextBuilder {
    this.hashtags = hashtags
    return this
  }

  fun withMentions(mentions: String?): ContextBuilder {
    this.mentions = mentions
    return this
  }

  fun withSupportsUnicode21(supportsUnicode21: Boolean): ContextBuilder {
    this.supportsUnicode21 = supportsUnicode21
    return this
  }

  fun withType(type: TimelineDataType): ContextBuilder {
    this.type = type
    return this
  }

  fun withYear(year: Int?): ContextBuilder {
    this.year = year
    return this
  }

  /**
   * Builds a Thymeleaf [Context] from the provided data.
   *
   * @return A Thymeleaf [Context].
   */
  fun build(): Context {
    checkNotNull(type) { "Timeline data type must be set." }

    val context = Context()
    context.setVariable("additionalContext", additionalContext)
    context.setVariable("description", description)
    context.setVariable("emoji", type!!.getEmoji(supportsUnicode21))
    context.setVariable("hashtags", hashtags)
    context.setVariable("mentions", mentions)
    context.setVariable("type", type.toString())
    context.setVariable("year", year)
    return context
  }
}
