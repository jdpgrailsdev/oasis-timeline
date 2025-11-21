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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.annotations.VisibleForTesting
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.support.ResourcePatternResolver
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

const val TIMELINE_DATA_FILE_LOCATION = "classpath*:/json/timelineData.json"
const val ADDITIONAL_TIMELINE_DATA_FILE_LOCATION = "classpath*:/json/additionalContextData.json"

/** Loads timeline data from the provided timeline data file. */
@SuppressFBWarnings(
  "BC_BAD_CAST_TO_ABSTRACT_COLLECTION",
  "EI_EXPOSE_REP",
  "EI_EXPOSE_REP2",
  "NP_NONNULL_RETURN_VIOLATION",
  "NP_NULL_ON_SOME_PATH",
)
class TimelineDataLoader(
  private val objectMapper: ObjectMapper,
  private val resourceResolver: ResourcePatternResolver,
) : InitializingBean {
  private lateinit var timelineData: List<TimelineData>
  private lateinit var additionalTimelineData: Map<String, List<String>>

  @Throws(IOException::class)
  override fun afterPropertiesSet() {
    loadTimelineData()
    loadAdditionalTimelineData()
  }

  /**
   * Fetches any additional history context for timeline data event.
   *
   * @param timelineData The timeline data event.
   * @return The additional history context associated with the timeline data event or an empty list
   *   if no additional context is available.
   */
  fun getAdditionalHistoryContext(timelineData: TimelineData): List<String> {
    val key = "${timelineData.date}, ${timelineData.year}_${timelineData.type}"
    return if (additionalTimelineData.containsKey(key)) {
      additionalTimelineData[key] ?: listOf()
    } else {
      listOf()
    }
  }

  /**
   * Fetches the historical timeline data events associated with the provided date.
   *
   * @param date The date possibly associated with timeline data event(s).
   * @return The list of associated timeline data events or an empty list if no such events exist.
   */
  fun getHistory(date: String): List<TimelineData> =
    timelineData
      .filter { t: TimelineData? -> !t!!.source.url.isNullOrBlank() }
      .filter { t: TimelineData? -> !t!!.isDisputed() }
      .filter { t: TimelineData? -> date == t!!.date }
      .sortedWith(Comparator.comparing(TimelineData::year))

  @Throws(IOException::class)
  private fun loadAdditionalTimelineData() {
    val resources = resourceResolver.getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION)

    if (resources.size > 0) {
      InputStreamReader(resources[0]!!.inputStream, Charset.defaultCharset()).use { reader ->
        this.additionalTimelineData =
          objectMapper.readValue(reader, AdditionalTimelineDataTypeReference())
      }
    } else {
      throw FileNotFoundException(
        "Unable to locate $ADDITIONAL_TIMELINE_DATA_FILE_LOCATION on the classpath.",
      )
    }
  }

  @Throws(IOException::class)
  private fun loadTimelineData() {
    val resources = resourceResolver.getResources(TIMELINE_DATA_FILE_LOCATION)

    if (resources.size > 0) {
      InputStreamReader(resources[0]!!.inputStream, Charset.defaultCharset()).use { reader ->
        this.timelineData = objectMapper.readValue(reader, TimelineDataTypeReference())
      }
    } else {
      throw FileNotFoundException("Unable to locate TIMELINE_DATA_FILE_LOCATION on the classpath.")
    }
  }

  @VisibleForTesting fun getTimelineData(): List<TimelineData> = timelineData
}

private class AdditionalTimelineDataTypeReference : TypeReference<Map<String, List<String>>>()

private class TimelineDataTypeReference : TypeReference<MutableList<TimelineData>>()
