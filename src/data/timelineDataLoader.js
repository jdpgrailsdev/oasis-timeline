import React from "react";
import timelineData from './timelineData';

export default {

    data: timelineData,

    descriptionToHTML: function(event) {
        return <span dangerouslySetInnerHTML={{ __html: event.description}} />;
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

    generateHistory: function(today) {
        return this.data.filter(function(event) {
            return event.date === today;
        });
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

    hasEventsForYear: function(year) {
        return this.data.filter(e => parseInt(e.year) === parseInt(year)).length > 0;
    }
};
