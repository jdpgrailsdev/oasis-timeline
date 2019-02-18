import * as React from "react";
import { HashLink as Link } from "react-router-hash-link";
import Footer from "./Footer";
import TimelineData from './data/timelineData.js';

class Sources extends React.Component<any, any> {

    compareSources(sourceA:any, sourceB:any) {
        if(sourceA != null && sourceA != undefined) {
            if(sourceB != null && sourceB != undefined) {
                return sourceA.localeCompare(sourceB);
            } else {
                return -1;
            }
        } else {
            return (sourceB != null && sourceB != undefined) ? 1 : 0;
        }
    }

    generateSources() {
        let sources = [...TimelineData.data]
            .filter(event => event.source != null && event.source != undefined)
            .sort((a,b) => this.compareSources(a.source, b.source))
            .map((event) => event.source);

        // Convert sources to set to remove duplicates
        return [...new Set(sources)]
            .filter(source => source != null && source.length > 0)
            .map((source, i) => <li key={"source_" + i}><a href={source} target="_blank" rel="noopener noreferrer">{source}</a></li>);
    }

    render() {
        return(
            <div className="main" id="top">
                <h2>Sources</h2>
                <div className="mainText">
                    <p>The following sites have been used for source information and/or contributed information to the Oasis Timeline project:</p>
                    <ul className="sources">
                        {this.generateSources()}
                    </ul>
                    <p>A very special thank you to the following sites and people:</p>
                    <ul className="sources">
                        <li><a href="https://www.microdotshop.co.uk/" target="_blank" rel="noopener noreferrer">Brian Cannon/Microdot Creative</a></li>
                        <li><a href="http://www.nigeldick.com/" target="_blank" rel="noopener noreferrer">Nigel Dick</a></li>
                        <li><a href="http://www.oasisinet.com/#!/gigs/list" target="_blank" rel="noopener noreferrer">Official Oasis Gigography</a></li>
                        <li><a href="http://www.oasis-recordinginfo.co.uk/" target="_blank" rel="noopener noreferrer">Oasis Recording Information</a></li>
                        <li><a href="https://turnupthevolume.blog" target="_blank" rel="noopener noreferrer">Turn Up The Volume Blog</a></li>
                        <li><a href="https://monobrowdemos.wordpress.com/" target="_blank" rel="noopener noreferrer">Oasis Demo Info</a></li>
                        <li><a href="http://live4ever.proboards.com/" target="_blank" rel="noopener noreferrer">Live4ever Forum</a></li>
                        <li><a href="https://www.reddit.com/r/oasis" target="_blank" rel="noopener noreferrer">Oasis sub-Reddit</a></li>
                        <li><a href="https://en.wikipedia.org/wiki/Oasis_(band)" target="_blank" rel="noopener noreferrer">Oasis Wikipedia Article(s)</a></li>
                        <li><a href="http://www.feelnumb.com/2010/10/13/girls-in-photos-oasis-cigarettes-alcohol-and-wonderwall-single-cover/" target="_blank" rel="noopener noreferrer">feelnumb - Girls In Photos Of Oasis “Cigarettes & Alcohol” And “Wonderwall” Single Cover</a></li>
                        <li><a href="https://www.ukcia.org/potculture/91/oasis2.html" target="_blank" rel="noopener noreferrer">Pot Culture: Liam Gallagher's exploits in 1996</a></li>
                        <li><a href="https://groups.google.com/forum/#!topic/alt.music.oasis/Is3w0VS6sbM" target="_blank" rel="noopener noreferrer">The Oasis Diaries - Q Magazine, September 1997</a></li>
                        <li><a href="http://oasisinterviews.blogspot.com/" target="_blank" rel="noopener noreferrer">Oasis Interviews Archive</a></li>
                    </ul>
                </div>
                <h3>Disclaimer</h3>
                <div className="mainText">The Oasis Timeline is not responsible for the content of source material and does not necessarily endorse the views expressed within.</div>
                <br />
                <br />
                <span className="backToTop">
                    <Link to="/sources#top">Back To Top</Link>
                </span>
                <Footer />
            </div>
        );
    }
}

export default Sources;