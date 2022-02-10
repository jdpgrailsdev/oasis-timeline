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

package com.jdpgrailsdev.oasis.timeline.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

/** Loads timeline data from the provided timeline data file. */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TimelineDataLoader implements InitializingBean {

  public static final String TIMELINE_DATA_FILE_LOCATION = "classpath*:/json/timelineData.json";

  public static final String ADDITIONAL_TIMELINE_DATA_FILE_LOCATION =
      "classpath*:/json/additionalContextData.json";

  private final ObjectMapper objectMapper;

  private final ResourcePatternResolver resourceResolver;

  private List<TimelineData> timelineData;

  private Map<String, List<String>> additionalTimelineData;

  public TimelineDataLoader(
      final ObjectMapper objectMapper, final ResourcePatternResolver resourceResolver) {
    this.objectMapper = objectMapper;
    this.resourceResolver = resourceResolver;
  }

  @Override
  public void afterPropertiesSet() throws IOException {
    loadTimelineData();
    loadAdditionalTimelineData();
  }

  /**
   * Fetches any additional history context for timeline data event.
   *
   * @param timelineData The timeline data event.
   * @return The additional history context associated with the timeline data event or an empty list
   *     if no additional context is available.
   */
  public List<String> getAdditionalHistoryContext(final TimelineData timelineData) {
    final String key =
        String.format(
            "%s, %d_%s",
            timelineData.getDate(), timelineData.getYear(), timelineData.getType().toString());
    if (additionalTimelineData.containsKey(key)) {
      return additionalTimelineData.get(key);
    } else {
      return ImmutableList.of();
    }
  }

  /**
   * Fetches the historical timeline data events associated with the provided date.
   *
   * @param date The date possibly associated with timeline data event(s).
   * @return The list of associated timeline data events or an empty list if no such events exist.
   */
  public List<TimelineData> getHistory(final String date) {
    return timelineData.stream()
        .filter(t -> StringUtils.hasText(t.getSource().getUrl()))
        .filter(t -> t.isDisputed() == null || !t.isDisputed())
        .filter(t -> date.equals(t.getDate()))
        .sorted(Comparator.comparing(TimelineData::getYear))
        .collect(Collectors.toList());
  }

  private void loadAdditionalTimelineData() throws IOException {
    final Resource[] resources =
        resourceResolver.getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION);

    if (resources.length > 0) {
      try (Reader reader =
          new InputStreamReader(resources[0].getInputStream(), Charset.defaultCharset())) {
        this.additionalTimelineData =
            objectMapper.readValue(reader, new AdditionalTimelineDataTypeReference());
      }
    } else {
      throw new FileNotFoundException(
          "Unable to locate " + ADDITIONAL_TIMELINE_DATA_FILE_LOCATION + " on the classpath.");
    }
  }

  private void loadTimelineData() throws IOException {
    final Resource[] resources = resourceResolver.getResources(TIMELINE_DATA_FILE_LOCATION);

    if (resources.length > 0) {
      try (Reader reader =
          new InputStreamReader(resources[0].getInputStream(), Charset.defaultCharset())) {
        this.timelineData = objectMapper.readValue(reader, new TimelineDataTypeReference());
      }
    } else {
      throw new FileNotFoundException(
          "Unable to locate " + TIMELINE_DATA_FILE_LOCATION + " on the classpath.");
    }
  }

  @VisibleForTesting
  List<TimelineData> getTimelineData() {
    return timelineData;
  }

  @VisibleForTesting
  Map<String, List<String>> getAdditionalTimelineData() {
    return additionalTimelineData;
  }

  private static class AdditionalTimelineDataTypeReference
      extends TypeReference<Map<String, List<String>>> {}

  private static class TimelineDataTypeReference extends TypeReference<List<TimelineData>> {}
}
