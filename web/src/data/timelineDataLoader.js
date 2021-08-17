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
import React from "react";
import additionalTimelineData from './additionalContextData'
import timelineData from './timelineData';

const timelineDataLoader = {

    additionalContextData: additionalTimelineData,

    data: timelineData,

    descriptionToHTML: function(event) {
        return <span dangerouslySetInnerHTML={{ __html: event.description}} />;
    },

    generateHistory: function(today) {
        return this.data.filter(function(event) {
            return event.date === today;
        });
    },

    generateKey: function(timestamp, type) {
        return timestamp + "_" + type;
    },

    getFirstYear: function() {
        return this.data.length > 0 ? parseInt(this.data[0].year, 10) : 0;
    },

    getLastYear: function() {
        return this.data.length > 0 ? parseInt(this.data[this.data.length - 1].year, 10) : 0;
    },

    getNumberOfEvents: function() {
        return this.data.length;
    },

    getNumberOfYears: function() {
        var firstYear = this.getFirstYear();
        var lastYear = this.getLastYear();
        return lastYear - firstYear;
    },

    getIcon: function(type) {
        switch(type) {
          case 'certifications':
              return 'star';
          case 'gigs':
              return 'music_note';
          case 'noteworthy':
             return 'announcement';
          case 'photo':
             return 'camera_alt';
          case 'recordings':
             return 'settings_voice';
          case 'releases':
             return 'album';
          case 'videos':
          default:
              return 'videocam';
        }
    },

    hasAdditionalContext: function(timestamp, type) {
        const key = this.generateKey(timestamp, type);
        return this.additionalContextData.hasOwnProperty(key);
    },

    hasEventsForYear: function(year) {
        return this.data.filter(e => parseInt(e.year) === parseInt(year)).length > 0;
    },
};

export default timelineDataLoader
