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
import com.google.common.collect.ImmutableList;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimelineDataLoader implements InitializingBean {

    private static final String TIMELINE_DATA_FILE_LOCATION_PATTERN = "classpath*:/json/timelineData.json";

    private static final String ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN = "classpath*:/json/additionalContextData.json";

    private final ObjectMapper objectMapper;

    private final ResourcePatternResolver resourceResolver;

    private List<TimelineData> timelineData;

    private Map<String, List<String>> additionalTimelineData;

    public TimelineDataLoader(final ObjectMapper objectMapper, final ResourcePatternResolver resourceResolver) {
        this.objectMapper = objectMapper;
        this.resourceResolver = resourceResolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadTimelineData();
        loadAdditionalTimelineData();
    }

    public List<String> getAdditionalHistoryContext(final TimelineData timelineData) {
        final String key = String.format("%s, %d_%s", timelineData.getDate(), timelineData.getYear(), timelineData.getType().name());
        if(additionalTimelineData.containsKey(key)) {
            return additionalTimelineData.get(key);
        } else {
            return ImmutableList.of();
        }
    }

    public List<TimelineData> getHistory(final String today) {
        return timelineData.stream()
                .filter(t -> today.equals(t.getDate()))
                .sorted((a, b) -> a.getYear().compareTo(b.getYear()))
                .collect(Collectors.toList());
    }

    private void loadAdditionalTimelineData() throws IOException {
        final Resource[] resources = resourceResolver.getResources(ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN);

        if(resources.length > 0) {
            try (final Reader reader = new InputStreamReader(resources[0].getInputStream(), Charset.defaultCharset())) {
                this.additionalTimelineData = objectMapper.readValue(reader, new TypeReference<Map<String, List<String>>>() {});
            }
        } else {
            throw new FileNotFoundException("Unable to locate " + ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN + " on the classpath.");
        }
    }

    private void loadTimelineData() throws IOException {
        final Resource[] resources = resourceResolver.getResources(TIMELINE_DATA_FILE_LOCATION_PATTERN);

        if(resources.length > 0) {
            try (final Reader reader = new InputStreamReader(resources[0].getInputStream(), Charset.defaultCharset())) {
                this.timelineData = objectMapper.readValue(reader, new TypeReference<List<TimelineData>>() {});
            }
        } else {
            throw new FileNotFoundException("Unable to locate " + TIMELINE_DATA_FILE_LOCATION_PATTERN + " on the classpath.");
        }
    }
}
