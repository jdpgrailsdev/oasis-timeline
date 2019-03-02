import * as React from "react";
import { HashLink as Link } from "react-router-hash-link";
import Disqus from 'disqus-react';
import BackToTop from "./shared/BackToTop";
import Footer from "./shared/Footer";

export default class Contributing extends React.Component<any, any> {

    render() {
        const disqusShortname = 'oasis-timeline';
        const disqusConfig = {
            url: 'https://www.oasis-timeline.com/#/contributing',
            identifier: 'contributing',
            title: 'Contributing'
        };

        return(
            <div className="main" id="top">
                <h2>Contributing</h2>
                <div className="mainText">
                    The goal of this site is to be as accurate as possible.  If you have information that may improve the timeline or have an event that you believe should be included in
                    the timeline, please do not hesitate to contact us!  The best place to do so is to add a comment to the <a href="" target="_blank" rel="noopener noreferrer">Oasis Timeline thread</a> (coming soon) on the <a href="http://live4ever.proboards.com/" target="_blank" rel="noopener noreferrer">Live4ever Forum</a>.
                    You may also make a comment <Link to="/contributing#disqus_thread">below</Link> via <a href="https://disqus.com" target="_blank" rel="noopener noreferrer">Disqus</a>.
                    <p>
                    Likewise, if you find an issue with the site itself (bug, typo or other problem), please create an issue over at the <a href="https://github.com/jdpgrailsdev/oasis-timeline/issues" target="_blank" rel="noopener noreferrer">site's repository in GitHub.</a>
                    </p>
                </div>
                <h3>Want List</h3>
                <div className="mainText">
                    Below are a list of events that are currently in need of confirmation/sources:
                    <ul>
                        <li>Filming date for the <i>Supersonic</i> (UK) music video</li>
                        <li>Filming date for the <i>Shakermaker</i> music video</li>
                        <li>Filming date for the <i>Live Forever</i> (US) music video</li>
                        <li>Filming date for the cancelled <i>Some Might Say</i> music video (believed to be some time in late March/early April 1995)</li>
                        <li>Filming date for the <i>Roll With It</i> music video</li>
                        <li>Filming date for the <i>Morning Glory</i> music video</li>
                        <li>Filming date for the <i>Stand By Me</i> music video</li>
                        <li>Filming date for the <i>Stand By Me</i> - Live at Bonehead's music video</li>
                        <li>Filming date for the <i>All Around the World</i> music video</li>
                        <li>Date of when Noel Gallagher moved to London (believed to be early 1994)</li>
                        <li>Date of when Liam Gallagher moved to London</li>
                        <li>Date of when Paul "Guigsy" McGuigan moved to London</li>
                    </ul>
                </div>
                <br />
                <BackToTop baseUri="/contributing" anchorId="top" />
                <br />
                <br />
                <div className="contribute">
                    <Disqus.DiscussionEmbed shortname={disqusShortname} config={disqusConfig} />
                </div>
                <Footer />
            </div>
        );
    }
}