import * as React from "react";
import Disqus from 'disqus-react';

class Contributing extends React.Component<any, any> {

    render() {
        const disqusShortname = 'oasis-timeline';
        const disqusConfig = {
            url: 'https://www.oasis-timeline.com/#/contributing',
            identifier: 'contributing',
            title: 'Contributing'
        };

        return(
            <div className="main">
                <h2>Contributing</h2>
                <div className="mainText">
                    The goal of this site is to be as accurate as possible.  If you have information that may improve the timeline or have an event that you believe should be included in
                    the timeline, please do not hesitate to contact us!  The best place to do so is to add a comment to the <a href="" target="_blank" rel="noopener noreferrer">Oasis Timeline thread</a> (coming soon) on the <a href="http://live4ever.proboards.com/" target="_blank" rel="noopener noreferrer">Live4ever Forum</a>.
                    You may also make a comment below via <a href="https://disqus.com" target="_blank" rel="noopener noreferrer">Disqus</a>.
                    <p>
                    Likewise, if you find an issue with the site itself (bug, typo or other problem), please create an issue over at the <a href="https://github.com/jdpgrailsdev/oasis-timeline/issues" target="_blank" rel="noopener noreferrer">site's repository in GitHub.</a>
                    </p>
                </div>
                <br />
                <br />
                <div className="contribute">
                    <Disqus.DiscussionEmbed shortname={disqusShortname} config={disqusConfig} />
                </div>
            </div>
        );
    }
}

export default Contributing;