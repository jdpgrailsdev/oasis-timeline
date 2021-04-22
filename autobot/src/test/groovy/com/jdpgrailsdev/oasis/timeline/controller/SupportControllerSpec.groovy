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
package com.jdpgrailsdev.oasis.timeline.controller

import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import com.jdpgrailsdev.oasis.timeline.data.Tweet
import com.jdpgrailsdev.oasis.timeline.util.DateUtils
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils

import spock.lang.Specification
import twitter4j.TwitterException

class SupportControllerSpec extends Specification {

    SupportController controller

    def setup() {
        TimelineDataLoader dataLoader = Mock(TimelineDataLoader) {
            getHistory(_) >> { [Mock(TimelineData), Mock(TimelineData)] }
        }
        TweetFormatUtils tweetFormatUtils = Mock(TweetFormatUtils) {
            generateTweet(_, _) >> { Mock(Tweet) }
        }
        controller = new SupportController(new DateUtils(), dataLoader, tweetFormatUtils)
    }

    def "test that when a request is made, all matching events are returned"() {
        setup:
            def date = '2020-08-04'
        when:
            def response = controller.getEvents(date)
        then:
            response.size() == 2
    }

    def "test that when a request is made but the controller is unable to generate the tweet text, the events are left out of the response"() {
        setup:
            TimelineDataLoader dataLoader = Mock(TimelineDataLoader) {
                getHistory(_) >> { [Mock(TimelineData), Mock(TimelineData)] }
            }
            TweetFormatUtils tweetFormatUtils = Mock(TweetFormatUtils) {
                generateTweet(_, _) >> { throw new TwitterException('test') }
            }
            controller = new SupportController(new DateUtils(), dataLoader, tweetFormatUtils)
            def date = '2020-08-04'
        when:
            def response = controller.getEvents(date)
        then:
            response.size() == 0
    }
}
