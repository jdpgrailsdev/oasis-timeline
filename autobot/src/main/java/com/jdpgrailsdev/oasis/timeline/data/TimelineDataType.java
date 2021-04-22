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

public enum TimelineDataType {
    certifications(Character.toChars(0x2B50)), // star emoji
    gigs(Character.toChars(0x1F3A4)), // microphone emoji
    noteworthy(Character.toChars(0x1F4F0)), // newspaper emoji
    photo(Character.toChars(0x1F4F8)), // camera with flash emoji
    recordings(Character.toChars(0x1F39B)), // control knobs emoji
    releases(Character.toChars(0x1F3B5)), // music note emoji
    videos(Character.toChars(0x1F3A5)); // movie camera emoji

    private char[] unicode;

    private TimelineDataType(final char[] unicode) {
        this.unicode = unicode;
    }

    public String getEmoji() {
        return new String(unicode);
    }
}
