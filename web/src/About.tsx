import * as React from "react";
import TimelineData from './data/timelineDataLoader.js';
import BackToTop from "./shared/BackToTop";
import Spacer from "./shared/Spacer";

export default class About extends React.Component<any, any> {

    render() {
        return(
            <div className="main" id="top">
                <h2>About</h2>
                <div className="mainText">
                    This site strives to be the most accurate timeline of events in the career of the British rock band <a href="http://www.oasisinet.com"  target="_blank" rel="noopener noreferrer">Oasis</a>.  To date, the timeline contains <b>{TimelineData.getNumberOfEvents()}</b> events covering <b>{TimelineData.getNumberOfYears()}</b> years.  If you have an event that you feel is important or have spotted a discrepancy, please head over to the <a href="#/contributing#disqus_thread">how to contribute page</a> to see how you can help make this the most complete historical timeline for Oasis!
                </div>
                <Spacer />
                <h3>What Is Included</h3>
                <div className="mainText">
                    The timeline documents key events, such as recording sessions, important gigs, releases and other noteworthy items.  The project is a work in progress and will continue to be updated as new information comes to light.
                    <br />
                    <br />
                    The <a href="#/timeline">timeline</a> maintained by this site has been compiled from various sources to date, including (but not limited to):
                    <ul>
                        <li>Officially released material</li>
                        <li>Contemporaneous news articles</li>
                        <li>Interviews</li>
                        <li>Biographies and other books about the band</li>
                        <li>Fan sites</li>
                        <li>Message boards</li>
                    </ul>
                </div>
                <Spacer />
                <h3>What is <b>NOT</b> Included</h3>
                <div className="mainText">
                    The timeline is not intended to be a gigography, though it will highlight gigs/performances that are important milestones or events.  It is also not intended to provide deep details about each event.  There are many official and fan sites that provide additional information about events in the timeline (such as recording session details, etc).
                </div>
                <Spacer />
                <h3 id="disclaimer">Disclaimer</h3>
                <div className="mainText">
                    <b>N.B.</b> Oasis Timeline is an unofficial fan website and is not related to Oasis, Epic Records, Sony Music, or Ignition Management.  All information contained within the site is soley for entertainment purposes only and is used within the scope of "fair use" purposes.
                    <p>Please see the <a href="#/sources">sources page</a> for a complete list of source material used to confirm the dates listed on this site.  The Oasis Timeline is not responsible for the content of source material and does not necessarily endorse the views expressed within.</p>
                </div>
                <br />
                <br />
                <BackToTop baseUri="/about" anchorId="top" />
                <br />
                <br />
                <br />
                <br />
            </div>
        );
    }
}