import React from 'react';

const sourceUtils = {

    generateSourceLink: function(event) {
        if (event.source && event.source.url) {
            return <span className="sourceLink"><a href={event.source.url} target="_blank" title={event.source.name + " - " + event.source.title} rel="noopener noreferrer"><i className="material-icons md-12">library_books</i></a></span>;
        } else {
            return <span></span>;
        }
    },

    isDisputed: function(event) {
        if(event.disputed) {
            return true;
        } else if(event.source && event.source.url) {
            return false;
        } else {
            return true;
        }
    },

    compareSources: function(sourceA, sourceB) {
        if(sourceA !== null && sourceA !== undefined) {
            if(sourceB !== null && sourceB !== undefined) {
                return this.getSourceTitle(sourceA).localeCompare(this.getSourceTitle(sourceB));
            } else {
                return -1;
            }
        } else {
            return (sourceB !== null && sourceB !== undefined) ? 1 : 0;
        }
    },

    getSourceTitle: function(source) {
        return (source.title !== undefined && source.title.length > 0) ? source.name + " - " + source.title : source.url;
    },

    generateSourceList: function(events) {
        let sources = [...events]
            .filter(event => event.source !== null && event.source !== undefined && Object.keys(event.source).length > 0)
            .sort((a,b) => this.compareSources(a.source, b.source))
            .map((event) => event.source);

        // Convert sources to set to remove duplicates
        return [...new Set(sources)]
            .filter(source => source != null && source.url.length > 0)
            .map((source, i) => <li key={"source_" + i}>{'\r\n'}<a href={source.url} target="_blank" rel="noopener noreferrer">{'\r\n'}{this.getSourceTitle(source)}{'\r\n'}</a>{'\r\n'}</li>);

    }
};

export default sourceUtils