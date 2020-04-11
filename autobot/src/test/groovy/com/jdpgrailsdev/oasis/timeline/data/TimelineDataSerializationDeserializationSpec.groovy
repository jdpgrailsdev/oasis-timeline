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

import com.fasterxml.jackson.databind.ObjectMapper

import spock.lang.Specification

class TimelineDataSerializationDeserializationSpec extends Specification {

    def "test that deserialization and serialization of a timeline data event works as expected"() {
        setup:
            def json = '''{
  "description" : "This is a description of an event 1.",
  "date" : "January 1",
  "source" : {
    "name" : "source1",
    "title" : "article1",
    "url" : "http://www.title.com/article1"
  },
  "title" : "Test Event 1",
  "type" : "certifications",
  "year" : 2020
}'''
            def objectMapper = new ObjectMapper()
        when:
            def timelineData = objectMapper.readValue(json, TimelineData)
        then:
            timelineData != null
            timelineData.getDate() == 'January 1'
            timelineData.getDescription() == 'This is a description of an event 1.'
            timelineData.getSource() instanceof TimelineDataSource
            timelineData.getSource().getName() == 'source1'
            timelineData.getSource().getTitle() == 'article1'
            timelineData.getSource().getUrl() == 'http://www.title.com/article1'
            timelineData.getTitle() == 'Test Event 1'
            timelineData.getType() == TimelineDataType.certifications
            timelineData.getYear() == 2020
        when:
            def json2 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(timelineData)
        then:
            json2 == json
    }
}
