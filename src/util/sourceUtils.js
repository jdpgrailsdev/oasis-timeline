import React from 'react';

export default {

    generateSourceLink: function(event) {
        if (event.source && event.source.url) {
            console.log('Source present');
            return <span className="sourceLink"><a href={event.source.url} target="_blank" title={event.source.name + " - " + event.source.title} rel="noopener noreferrer"><i className="material-icons md-12">library_books</i></a></span>;
        } else {
            console.log('no source');
            return <span></span>;
        }
//        return (event.source && event.source.url) ? <span className="sourceLink"><a href={event.source.url} target="_blank" title={event.source.name + " - " + event.source.title} rel="noopener noreferrer"><i className="material-icons md-12">library_books</i></a></span> : <span></span>;
    },

    isDisputed: function(event) {
        if(event.disputed) {
            return true;
        } else if(event.source && event.source.url) {
            return false;
        } else {
            return true;
        }
    }
};