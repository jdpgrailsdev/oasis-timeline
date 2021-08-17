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
package com.jdpgrailsdev.oasis.timeline.util

import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import org.thymeleaf.context.Context
import spock.lang.Specification

class ContextBuilderSpec extends Specification {

    def "test that when all fields are set on the builder, the context can be built"() {
        setup:
            String additionalContext = 'additional context'
            String description = 'description'
            String hashtags = '#hashtags'
            String mentions = '@mentions'
            TimelineDataType type = TimelineDataType.CERTIFICATIONS
            Integer year = 2021
        when:
            Context context = new ContextBuilder()
                .withAdditionalContext(additionalContext)
                .withDescription(description)
                .withHashtags(hashtags)
                .withMentions(mentions)
                .withType(type)
                .withYear(year)
                .build()
        then:
            context != null
            context.getVariable('additionalContext') == additionalContext
            context.getVariable('description') == description
            context.getVariable('emoji') == type.getEmoji()
            context.getVariable('hashtags') == hashtags
            context.getVariable('mentions') == mentions
            context.getVariable('type') == type.toString()
            context.getVariable('year') == year
    }

    def "test that when the type field is not set, the context can not be built"() {
        setup:
            String additionalContext = 'additional context'
            String description = 'description'
            String hashtags = '#hashtags'
            String mentions = '@mentions'
            Integer year = 2021
        when:
            Context context = new ContextBuilder()
                    .withAdditionalContext(additionalContext)
                    .withDescription(description)
                    .withHashtags(hashtags)
                    .withMentions(mentions)
                    .withYear(year)
                    .build()
        then:
            thrown(NullPointerException)
    }
}
