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

import com.jdpgrailsdev.oasis.timeline.config.TweetContext
import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataSource
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType
import com.jdpgrailsdev.oasis.timeline.data.Tweet

import org.thymeleaf.ITemplateEngine

import spock.lang.Specification
import spock.lang.Unroll

class TweetFormatUtilsSpec extends Specification {

    ITemplateEngine templateEngine

    TweetContext tweetContext

    TweetFormatUtils utils

    def setup() {
        templateEngine = Mock(ITemplateEngine) {
            process(_,_) >> { 'This is a template string.' }
        }
        tweetContext = Mock(TweetContext) {
            getHashtags() >> { ['hashtag1', 'hashtag2'] as Set }
            getMentions() >> { [A:'a'] }
            getUncapitalizeExclusions() >> { ['Proper Noun', 'Oasis'] as Set }
        }
        utils = new TweetFormatUtils(templateEngine, tweetContext)
    }

    @Unroll
    def "test that when a event text '#text' is used to generate status updates, a tweet is created with the expected number of messages #expected"() {
        setup:
            templateEngine = Mock(ITemplateEngine) {
                process(_,_) >> { text }
            }
            utils = new TweetFormatUtils(templateEngine, tweetContext)
            def timelineData = Mock(TimelineData) {
                getDate() >> { 'January 1' }
                getDescription() >> { text }
                getSource() >> { Mock(TimelineDataSource) }
                getTitle() >> { 'title' }
                getType() >> { TimelineDataType.GIGS }
                getYear() >> { 2020 }
            }
            def additionalContext = ['additional context']
        when:
            def tweet = utils.generateTweet(timelineData, additionalContext)
        then:
            tweet != null
            tweet.messages.size() == expected
            tweet.messages.each { message ->
                message.length() <= Tweet.TWEET_LIMIT
            }
        where:
            text                                                                || expected
            'A word'.multiply(200)                                              || 6
            'A word'                                                            || 1
    }

    @Unroll
    def "test that when a description '#description' is prepared for use in the template, the expected result '#expected' is generated"() {
        expect:
            utils.prepareDescription(description) == expected
        where:
            description                         || expected
            'Proper Noun does something.'       || 'Proper Noun does something'
            'Proper Noun does something'        || 'Proper Noun does something'
            'This is a sentence.'               || 'this is a sentence'
            'This is a sentence'                || 'this is a sentence'
            'Oasis are the best.'               || '@Oasis are the best'
            'Sentence with Oasis in it.'        || 'sentence with @Oasis in it'
            ''                                  || ''
            null                                || null
    }

    @Unroll
    def "test that when mentions are generated for description '#description' based on mention map #mentions, the expected string of mentions '#expected' is returned"() {
        setup:
            TweetContext tweetContext = Mock(TweetContext) {
                getMentions() >> { mentions }
            }
            utils = new TweetFormatUtils(templateEngine, tweetContext)
        when:
            def mentionsString = utils.generateMentions(description)
        then:
            mentionsString == expected
        where:
            description                         | mentions                          || expected
            'John Doe did something today.'     | ['john_doe':'johndoe']            || '@johndoe'
            'later, John Doe did it again.'     | ['john_doe':'johndoe']            || '@johndoe'
            'Jane Doe also did something.'      | ['john_doe':'johndoe']            || ''
            'John Doe did something today.'     | [:]                               || ''
            'John "Jdoe" Doe did something.'    | ['john_jdoe_doe':'johndoe']       || '@johndoe'
            'The Queen of England'              | ['queen_of_england':'hrm_uk']     || '@hrm_uk'
    }
}
