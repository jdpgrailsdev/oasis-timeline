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

package com.jdpgrailsdev.oasis.timeline.config

import com.jdpgrailsdev.oasis.timeline.data.PostTarget
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.core.Ordered

@SuppressFBWarnings("EI_EXPOSE_REP2")
open class SocialContext
  @ConstructorBinding
  constructor(
    private var hashtags: Set<String> = emptySet(),
    private var mentions: Map<String, String> = emptyMap(),
    private var uncapitalizeExclusions: Set<String> = emptySet(),
  ) {
    open fun getHashtags(): Set<String> = hashtags.sorted().toSet()

    open fun getMentions(): Map<String, String> = mentions.toMap()

    open fun getUncapitalizeExclusions(): Set<String> = uncapitalizeExclusions.toSet()

    // Setters are here to work with Spring's configuration properties injection
    fun setHashtags(hashtags: Set<String>) {
      this.hashtags = hashtags
    }

    fun setMentions(mentions: Map<String, String>) {
      this.mentions = mentions
    }

    fun setUncapitalizeExclusions(uncapitalizeExclusions: Set<String>) {
      this.uncapitalizeExclusions = uncapitalizeExclusions
    }

    open fun supports(postTarget: PostTarget): Boolean = true
  }

/** Custom context used to generate templates via Thymeleaf. */
@ConfigurationProperties(prefix = "post.context")
class PostContext :
  SocialContext(),
  Ordered {
  override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}

/** Custom context used to generate Bluesky templates via Thymeleaf. */
@ConfigurationProperties(prefix = "bluesky.context")
class BlueSkyContext :
  SocialContext(),
  Ordered {
  override fun supports(postTarget: PostTarget): Boolean = postTarget == PostTarget.BLUESKY

  override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE
}

/** Custom context used to generate Mastodon templates via Thymeleaf. */
@ConfigurationProperties(prefix = "mastodon.context")
class MastodonContext :
  SocialContext(),
  Ordered {
  override fun supports(postTarget: PostTarget): Boolean = postTarget == PostTarget.MASTODON

  override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE
}

/** Custom context used to generate Twitter templates via Thymeleaf. */
@ConfigurationProperties(prefix = "tweet.context")
class TweetContext :
  SocialContext(),
  Ordered {
  override fun supports(postTarget: PostTarget): Boolean = postTarget == PostTarget.TWITTER

  override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE
}
