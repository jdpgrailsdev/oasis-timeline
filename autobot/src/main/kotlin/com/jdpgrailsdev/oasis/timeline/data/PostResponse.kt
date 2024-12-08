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

/**
 * Global function to create [PostResponse] objects from a raw social network response.
 *
 * @param response The raw response from a social network.
 * @return a new [PostResponse] wrapper object.
 */
fun <T> createPostResponse(response: T?): PostResponse<T> = PostResponse(response)

/** Wrapper data class for responses returned by social networks. */
data class PostResponse<T>(
  val response: T?,
)
