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
        return parseInt(this.data[0].year, 10);
    },

    getLastYear: function() {
        return parseInt(this.data[this.data.length - 1].year, 10);
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
