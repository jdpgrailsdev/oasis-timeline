import * as React from "react";
import { Link } from "react-router-dom";
import TimelineData from './data/timelineDataLoader.js';
import BackToTop from "./shared/BackToTop";
import Spacer from "./shared/Spacer";

export default class About extends React.Component<any, any> {

    render() {
        return(
            <div className="main" id="top">
                <h2>About</h2>
                <div className="mainText">
                    This site strives to be the most accurate timeline of events in the career of the British rock band <span className="oasis"><a href="http://www.oasisinet.com"  target="_blank" rel="noopener noreferrer">Oasis</a></span>.  To date, the timeline contains <b>{TimelineData.getNumberOfEvents()}</b> events covering <b>{TimelineData.getNumberOfYears()}</b> years.  If you have an event that you feel is important or have spotted a discrepancy, please head over to the <a href="#/contributing#disqus_thread">how to contribute page</a> to see how you can help make this the most complete historical timeline for <span className="oasis">Oasis</span>!
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
                <h3>Terms and Conditions</h3>
                <div className="mainText">
                    <p>Please visit <Link to="/terms#top">terms and conditions page</Link> for more information.</p>
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