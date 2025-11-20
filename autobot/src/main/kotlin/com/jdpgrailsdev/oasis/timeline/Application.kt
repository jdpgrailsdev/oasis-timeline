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

package com.jdpgrailsdev.oasis.timeline

import com.jdpgrailsdev.oasis.timeline.config.ApplicationConfiguration
import com.newrelic.api.agent.NewRelic
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
  // Notice any uncaught exceptions at runtime.
  Thread.setDefaultUncaughtExceptionHandler { _: Thread, throwable: Throwable ->
    logger.error(throwable) { throwable.message }
    NewRelic.noticeError(throwable)
  }

  val app = SpringApplication(ApplicationConfiguration::class.java)
  app.applicationStartup = BufferingApplicationStartup(2048)
  app.run(*args)
}
