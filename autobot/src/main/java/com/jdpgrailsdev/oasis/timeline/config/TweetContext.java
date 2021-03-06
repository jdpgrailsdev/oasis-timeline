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
package com.jdpgrailsdev.oasis.timeline.config;


import com.jdpgrailsdev.oasis.timeline.util.Generated;
import java.util.Map;
import java.util.Set;

/** Custom context used to generate templates via Thymeleaf. */
public class TweetContext {

    private Set<String> hashtags;

    private Map<String, String> mentions;

    private Set<String> uncapitalizeExclusions;

    @Generated
    public Set<String> getHashtags() {
        return hashtags;
    }

    @Generated
    public Map<String, String> getMentions() {
        return mentions;
    }

    @Generated
    public Set<String> getUncapitalizeExclusions() {
        return uncapitalizeExclusions;
    }

    @Generated
    public void setHashtags(final Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    @Generated
    public void setMentions(final Map<String, String> mentions) {
        this.mentions = mentions;
    }

    @Generated
    public void setUncapitalizeExclusions(final Set<String> uncapitalizeExclusions) {
        this.uncapitalizeExclusions = uncapitalizeExclusions;
    }
}
