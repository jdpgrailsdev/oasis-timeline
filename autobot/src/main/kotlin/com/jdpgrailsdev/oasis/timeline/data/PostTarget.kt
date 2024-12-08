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

private const val POST_LIMIT: Int = 300
private const val TWEET_LIMIT: Int = 280

enum class PostTarget(
  val limit: Int,
) {
  BLUESKY(limit = POST_LIMIT),
  TWITTER(limit = TWEET_LIMIT),
  ;

  fun displayName(capitalize: Boolean = true): String = if (capitalize) capitalize() else name.lowercase()

  private fun capitalize() = name.lowercase().replaceFirstChar { c -> c.uppercase() }
}
