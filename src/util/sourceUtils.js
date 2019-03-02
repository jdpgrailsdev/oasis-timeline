import React from 'react';

export default {

    generateSourceLink: function(event) {
        return event.source ? <span className="sourceLink"><a href={event.source} target="_blank" rel="noopener noreferrer"><i className="material-icons md-12">library_books</i></a></span> : <span></span>;
    },

    isDisputed: function(event) {
        if(event.disputed) {
            return true;
        } else if(event.source) {
            return false;
        } else {
            return true;
        }
    }
};