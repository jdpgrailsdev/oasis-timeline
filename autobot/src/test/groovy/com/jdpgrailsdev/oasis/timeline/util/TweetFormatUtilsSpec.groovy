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

import org.thymeleaf.ITemplateEngine

import spock.lang.Specification
import spock.lang.Unroll

class TweetFormatUtilsSpec extends Specification {

    ITemplateEngine templateEngine

    TweetFormatUtils utils

    def setup() {
        templateEngine = Mock(ITemplateEngine) {
            process(_,_) >> { 'This is a template string.' }
        }
        utils = new TweetFormatUtils(templateEngine, ['Proper Noun'] as Set)
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
            ''                                  || ''
            null                                || null
    }

    def "test that when a string that is under the tweet limit is split, the entire string is returned"() {
        setup:
            def text = 'Under the limit'
        when:
            def result = utils.splitStatusText(text)
        then:
            result.size() == 1
            result.first() == text
    }

    def "test that an event that exceeds the limit of characters is appropriately broken up into individual parts"() {
        setup:
            def text = 'On this date in 1994, after back and forth with fans during a gig at Riverside in Newcastle, UK, a fight breaks out on stage resulting in Noel Gallager damaging a 1960\'s sunburst Gibson Les Paul guitar given to him by Johnny Marr of The Smiths.  The band refuse to continue the show after 5 songs, leading to fans surrounding the band\'s van.  Noel also would require stitches after the attack.  The setlist includes the following songs: Columbia, Shakermaker, Fade Away, Digsy\'s Dinner, Live Forever, Bring It On Down (Noel Gallagher attacked on stage during song).'
        when:
            def result = utils.splitStatusText(text)
        then:
            result.size() == Math.ceil(text.length()/TweetFormatUtils.TWEET_LIMIT)
            result[0].length() <= TweetFormatUtils.TWEET_LIMIT
            result[0] == 'On this date in 1994, after back and forth with fans during a gig at Riverside in Newcastle, UK, a fight breaks out on stage resulting in Noel Gallager damaging a 1960\'s sunburst Gibson Les Paul guitar given to him by Johnny Marr of The Smiths.  The band refuse to continue the...'
            result[1].length() <= TweetFormatUtils.TWEET_LIMIT
            result[1] == '...show after 5 songs, leading to fans surrounding the band\'s van.  Noel also would require stitches after the attack.  The setlist includes the following songs: Columbia, Shakermaker, Fade Away, Digsy\'s Dinner, Live Forever, Bring It On Down (Noel Gallagher attacked on stage...'
            result[2].length() <= TweetFormatUtils.TWEET_LIMIT
            result[2] == '...during song).'
    }
}
