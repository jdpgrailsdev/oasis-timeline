import * as React from "react";
import BackToTop from "./shared/BackToTop";
import TimelineData from './data/timelineDataLoader.js';
import TodayInHistory from "./shared/TodayInHistory";

export default class Home extends React.Component<any, any> {

    render() {
        return(
            <div className="main" id="top">
                <br />
                <br />
                <div className="quote group">
                    <div className="quote-container">
                        <blockquote>
                            <p>This is history!  Right here, right now - this is history!</p>
                        </blockquote>
                        <cite><span>Noel Gallagher</span><br />
                            Knebworth, Hertfordshire, UK<br />
                            August 10, 1996
                        </cite>
                    </div>
                </div>
                <br />
                <div className={"mainText centered"}>
                    The history of <span className="oasis"><a href="http://www.oasisinet.com"  target="_blank" rel="noopener noreferrer">Oasis</a></span> as documented through <b>{TimelineData.getNumberOfEvents()}</b> events spanning <b>{TimelineData.getNumberOfYears()}</b> years.
                </div>
                <br />
                <TodayInHistory />
                <br />
                <br />
                <BackToTop baseUri="/" anchorId="top" />
                <br />
                <br />
            </div>
        );
    }
}