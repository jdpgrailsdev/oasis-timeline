import * as React from "react";
import TimelineData from './data/timelineData.js';

class Sources extends React.Component<any, any> {

    generateSources() {
        const sources = [];

        for(var i in TimelineData.data) {
            const event = TimelineData.data[i];
            if(event.source != null) {
                sources.push(<li><a href={event.source} target="_blank" rel="noopener noreferrer">{event.source}</a></li>);
            }
        }

        return sources;
    }

    render() {
        return(
            <div className="main">
                <h2>Sources</h2>
                <div className="mainText">
                    <p>The following people and sites have been used for source information and/or contributed information to the Oasis Timeline project:</p>
                    <ul>
                        <li><a href="http://www.oasisinet.com/#!/gigs/list" target="_blank" rel="noopener noreferrer">Official Oasis Gigography</a></li>
                        <li><a href="http://www.oasis-recordinginfo.co.uk/" target="_blank" rel="noopener noreferrer">Oasis Recording Information</a></li>
                        <li><a href="https://turnupthevolume.blog" target="_blank" rel="noopener noreferrer">Turn Up The Volume Blog</a></li>
                        <li><a href="https://monobrowdemos.wordpress.com/" target="_blank" rel="noopener noreferrer">Oasis Demo Info</a></li>
                        <li><a href="http://live4ever.proboards.com/" target="_blank" rel="noopener noreferrer">Live4ever Forum</a></li>
                        <li><a href="https://www.reddit.com/r/oasis" target="_blank" rel="noopener noreferrer">Oasis sub-Reddit</a></li>
                        <li><a href="https://en.wikipedia.org/wiki/Oasis_(band)" target="_blank" rel="noopener noreferrer">Oasis Wikipedia Article(s)</a></li>
                        <li><a href="http://www.feelnumb.com/2010/10/13/girls-in-photos-oasis-cigarettes-alcohol-and-wonderwall-single-cover/" target="_blank" rel="noopener noreferrer">feelnumb - Girls In Photos Of Oasis “Cigarettes & Alcohol” And “Wonderwall” Single Cover</a></li>
                        <li><a href="https://www.ukcia.org/potculture/91/oasis2.html" target="_blank" rel="noopener noreferrer">Pot Culture: Liam Gallagher's exploits in 1996</a></li>
                        {this.generateSources()}
                    </ul>
                </div>
            </div>
        );
    }
}

export default Sources;