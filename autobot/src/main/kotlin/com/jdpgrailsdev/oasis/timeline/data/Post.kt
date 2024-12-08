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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.common.base.Splitter
import com.google.common.collect.Lists
import org.springframework.util.StringUtils
import kotlin.math.floor

/** Accounts for the emoji and elipses added to multipart tweets. */
private const val ADDITIONAL_CHARACTERS = 4

private const val REDUCTION_PERCENTAGE = 0.85

fun matchBlankSpace(c: Char): Boolean = c <= ' '

/** Represents a post to a social network. */
class Post(
  private val text: String,
  private val limit: Int,
) {
  companion object {
    /**
     * Creates a new post from the provided text.
     *
     * @param text The text of the Post.
     * @return a new [Post] instance
     * @throws PostException if the provided text is blank.
     */
    @JvmStatic
    @Throws(PostException::class)
    fun createPost(
      text: String?,
      limit: Int,
    ): Post {
      if (StringUtils.hasText(text)) {
        return Post(text = text!!, limit = limit)
      } else {
        throw PostException("Post message may not be blank.")
      }
    }
  }

  private val messages: List<String>

  init {
    messages =
      if (text.length > limit) {
        splitMessage(text)
      } else {
        listOf(text)
      }
  }

  @JsonIgnore fun getMainPost(): String = messages.first()

  /**
   * Retrieves the messages associated with this post. Posts may have multiple parts.
   *
   * @return The message parts associated with the post.
   */
  fun getMessages(): List<String> = messages.toList()

  /**
   * Returns the replies for the main post if the message exceeds the post limit.
   *
   * @return The replies to the main post or an empty list if no replies are necessary.
   */
  @JsonIgnore fun getReplies(): List<String> = messages.stream().skip(1).toList()

  private fun splitMessage(text: String): List<String> {
    val tweets: MutableList<String> = Lists.newArrayList()
    var size = text.length

    while (size >= (limit - ADDITIONAL_CHARACTERS)) {
      size = floor(size * REDUCTION_PERCENTAGE).toInt()
    }

    val builder = StringBuilder()
    val words = Splitter.on(' ').splitToList(text)
    for (word in words) {
      if ((builder.length + word.length) <= size) {
        builder.append(' ')
      } else {
        val useEllipses = !builder.toString().trim(::matchBlankSpace).endsWith(".")
        if (useEllipses) {
          builder.append("...")
        }
        tweets.add(builder.toString().trim(::matchBlankSpace))
        builder.setLength(0)
        if (useEllipses) {
          builder.append("... ")
        }
      }
      builder.append(word)
    }
    tweets.add(builder.toString().trim(::matchBlankSpace))
    return tweets
  }
}

class PostException(
  message: String,
) : Exception(message)
