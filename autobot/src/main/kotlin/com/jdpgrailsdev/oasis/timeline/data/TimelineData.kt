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
import com.fasterxml.jackson.annotation.JsonInclude
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

@SuppressWarnings(
  "PMD.DataClass",
  "PMD.CognitiveComplexity",
  "PMD.CyclomaticComplexity",
  "PMD.NPathComplexity",
)
@SuppressFBWarnings("EI_EXPOSE_REP", "EI_EXPOSE_REP2")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TimelineData(
  val description: String,
  val date: String,
  val disputed: Boolean,
  val source: TimelineDataSource,
  val title: String,
  val type: TimelineDataType,
  val year: Int,
) {
  @JsonIgnore fun isDisputed() = disputed
}
