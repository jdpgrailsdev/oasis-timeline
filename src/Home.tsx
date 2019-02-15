import * as React from "react";
import Footer from "./Footer";
import TimelineData from './data/timelineData.js';
import TodayInHistory from "./TodayInHistory";

class Home extends React.Component<any, any> {

    render() {
        return(
            <div className="main">
                <h2>Welcome</h2>
                <div className="mainText">
                    This site strives to be the canonical timeline of events in the career of the British rock band <a href="http://www.oasisinet.com"  target="_blank" rel="noopener noreferrer">Oasis</a>.  To date, the timeline contains <b>{TimelineData.getNumberOfEvents()}</b> events covering <b>{TimelineData.getNumberOfYears()}</b> years.  If you have an event that you feel is important or have spotted a discrepancy, please head over to the <a href="#/contributing#disqus_thread">how to contribute page</a> to see how you can help make this the most complete timeline for Oasis!
                </div>
                <TodayInHistory />
                <h3>What Is Included</h3>
                <div className="mainText">
                    The timeline documents key envets, such as recording sessions, important gigs, releases and other noteworthy items.  The project is a work in progress and will continue to be updated as new information comes to light.
                    <p>
                        The <a href="#/timeline">timeline</a> maintained by this site has been compiled from various sources to date, including (but not limited to):
                        <ul>
                            <li>Officially released material</li>
                            <li>Contemporaneous news articles</li>
                            <li>Interviews</li>
                            <li>Biographies and other books about the band</li>
                            <li>Fan sites</li>
                            <li>Message boards</li>
                        </ul>
                    </p>
                </div>
                <h3>What is <b>NOT</b> Included</h3>
                <div className="mainText">
                    The timeline is not intended to be a gigography, though it will highlight gigs/performances that are important milestones or events.  It is also not intended to provide deep details about each event.  There are many official and fan sites that provide additional information about events in the timeline (such as recording session details, etc).
                </div>
                <h3>Disclaimers</h3>
                <div className="mainText">
                    <b>N.B.</b> Oasis Timeline is an unofficial fan website and is not related to Oasis, Epic Records, Sony Music, or Ignition Management.  All information contained within the site is soley for entertainment purposes only and is used within the scope of "fair use" purposes.
                    <p>Please see the <a href="#/sources">sources page</a> for a complete list of source material used to confirm the dates listed on this site.  The Oasis Timeline is not responsible for the content of source material and does not necessarily endorse the views expressed within.</p>
                </div>
                <Footer />
            </div>
        );
    }
}

export default Home;