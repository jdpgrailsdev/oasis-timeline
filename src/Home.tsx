import * as React from "react";
import TimelineData from './data/timelineData.js';

class Home extends React.Component<any, any> {

    render() {
        return(
            <div className="main">
                <h2>Welcome</h2>
                <div className="mainText">
                This site strives to be the canonical timeline of events in the career of the British rock band <a href="https://www.oasisinet.com" target="_blank">Oasis</a>.  Key events include recording sessions,
                important gigs, releases and other noteworthy items.  The project is a work in progress and will continue to be updated as new information comes to light.  To date, the timeline contains <b>{TimelineData.getNumberOfEvents()}</b> events covering <b>{TimelineData.getNumberOfYears()}</b> years.
                The <a href="#/timeline">timeline</a> maintained by this site has been compiled from various sources to date, including (but not limited to):
                <ul>
                    <li>Officially released material</li>
                    <li>Interviews</li>
                    <li>Biographies and other books about the band</li>
                    <li>Fan sites</li>
                    <li>Message boards</li>
                </ul>
                Please head over to the <a href="#/contributing">how to contribute page</a> to see how you can help make this the most complete timeline for Oasis!
                <p>
                <b>N.B.</b> Oasis Timeline is an unofficial fan website and is not related to Oasis, Sony Music, or Ignition Management.
                </p>
                </div>
            </div>
        );
    }
}

export default Home;