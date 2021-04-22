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
package com.jdpgrailsdev.oasis.timeline.util;


import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;
import org.thymeleaf.context.Context;

public class ContextBuilder {

    private String additionalContext;

    private String description;

    private String hashtags;

    private String mentions;

    private TimelineDataType type;

    private Integer year;

    public ContextBuilder withAdditionalContext(final String additionalContext) {
        this.additionalContext = additionalContext;
        return this;
    }

    public ContextBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public ContextBuilder withHashtags(final String hashtags) {
        this.hashtags = hashtags;
        return this;
    }

    public ContextBuilder withMentions(final String mentions) {
        this.mentions = mentions;
        return this;
    }

    public ContextBuilder withType(final TimelineDataType type) {
        this.type = type;
        return this;
    }

    public ContextBuilder withYear(final Integer year) {
        this.year = year;
        return this;
    }

    public Context build() {
        final Context context = new Context();
        context.setVariable("additionalContext", additionalContext);
        context.setVariable("description", description);
        context.setVariable("emoji", type.getEmoji());
        context.setVariable("hashtags", hashtags);
        context.setVariable("mentions", mentions);
        context.setVariable("type", type.name());
        context.setVariable("year", year);
        return context;
    }
}
