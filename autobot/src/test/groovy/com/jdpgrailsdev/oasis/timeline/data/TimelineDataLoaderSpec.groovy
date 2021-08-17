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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver

import spock.lang.Specification

class TimelineDataLoaderSpec extends Specification {

    ObjectMapper objectMapper

    def setup() {
        objectMapper = new ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    }

    def "test that when the timeline data is loaded on bean creation, the timeline data field is populated"() {
        setup:
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
        when:
            loader.loadTimelineData()
        then:
            loader.timelineData != null
            loader.timelineData.size() == 9
            loader.timelineData.first().getDate() != null
            loader.timelineData.first().getDescription() != null
            loader.timelineData.first().getSource() != null
            loader.timelineData.first().getSource().getName() != null
            loader.timelineData.first().getSource().getTitle() != null
            loader.timelineData.first().getSource().getUrl() != null
            loader.timelineData.first().getTitle() != null
            loader.timelineData.first().getType() != null
            loader.timelineData.first().getYear() != null
    }

    def "test that when the timeline data is filtered to a given day, the correct entries are returned"() {
        setup:
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
            loader.afterPropertiesSet()
            String today = 'January 1'
         when:
            def result = loader.getHistory(today)
         then:
             result.size() == 4
    }

    def "test that when the timeline data is filtered to a given day and the timeline data has additional context, the additional context can be retrieved"() {
        setup:
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
            loader.afterPropertiesSet()
            String today = 'January 1'
         when:
            def result = loader.getHistory(today)
         then:
             result.size() == 4
             result.each { timelineData ->
                 def additional = loader.getAdditionalHistoryContext(timelineData)
                 if(TimelineDataType.GIGS == timelineData.getType()) {
                     additional != null
                     additional.size() == 3
                     additional == ['Song 1', 'Song 2', 'Song 3']
                 } else {
                     additional != null
                     additional.size() == 0
                 }
            }
    }

    def "test that when a match is found in the additional data, the additional data is returned"() {
        setup:
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
            loader.afterPropertiesSet()
            TimelineData timelineData = Mock(TimelineData) {
                getDate() >> { 'January 1'}
                getYear() >> { 2020 }
                getType() >> { TimelineDataType.GIGS}
            }
        when:
            List<String> additionalContext = loader.getAdditionalHistoryContext(timelineData)
        then:
            additionalContext.size() == 3
            additionalContext[0] == 'Song 1'
            additionalContext[1] == 'Song 2'
            additionalContext[2] == 'Song 3'
    }

    def "test that when the timeline data file is unable to be located, an exception is thrown"() {
        setup:
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(_) >> { [] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
        when:
            loader.afterPropertiesSet()
        then:
            thrown FileNotFoundException
    }

    def "test that when the additional timeline data file is unable to be located, an exception is thrown"() {
        setup:
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION) >> { [] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(objectMapper, resolver)
        when:
            loader.afterPropertiesSet()
        then:
            thrown FileNotFoundException
    }
}