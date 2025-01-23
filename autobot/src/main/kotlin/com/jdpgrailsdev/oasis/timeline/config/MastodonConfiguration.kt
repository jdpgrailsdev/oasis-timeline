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

import com.jdpgrailsdev.oasis.timeline.util.MastodonApiUtils
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import social.bigbone.MastodonClient
import social.bigbone.api.Scope

@Configuration
class MastodonConfiguration {
  @Bean fun mastodonScope(): Scope = Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.PUSH.ALL)

  @Bean
  @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
  fun mastodonClient(
    @Value("\${mastodon.access-token}") accessToken: String,
    @Value("\${mastodon.instance-name}") instanceName: String,
    @Value("\${mastodon.connect.timeout.seconds:10}") connectTimeoutSeconds: Long,
    @Value("\${mastodon.read.timeout.seconds:30}") readTimeoutSeconds: Long,
    @Value("\${mastodon.port}") port: Int?,
    environment: Environment,
  ): MastodonClient {
    val builder =
      MastodonClient
        .Builder(instanceName)
        .accessToken(accessToken)
        .setConnectTimeoutSeconds(connectTimeoutSeconds)
        .setReadTimeoutSeconds(readTimeoutSeconds)

    port?.let { builder.withPort(port) }

    if (environment.activeProfiles.contains("test")) {
      builder.withHttpsDisabled()
    }
    return builder.build()
  }

  @Bean
  fun mastodonApiUtils(
    @Value("\${mastodon.account-id}") accountId: String,
    mastodonClient: MastodonClient,
  ): MastodonApiUtils = MastodonApiUtils(accountId = accountId, client = mastodonClient)
}
