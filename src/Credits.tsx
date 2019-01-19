import * as React from "react";

class Credits extends React.Component<any, any> {

    render() {
        return(
            <div className="main">
                <h2>Credits</h2>
                <div className="mainText">
                    <p>The following people and sites have been used for source information or contributed information to the Oasis Timeline project:</p>
                    <ul>
                        <li><a href="http://www.oasisinet.com/#!/gigs/list" target="_blank">Official Oasis Gigography</a></li>
                        <li><a href="http://www.oasis-recordinginfo.co.uk/" target="_blank">Oasis Recording Information</a></li>
                        <li><a href="https://turnupthevolume.blog" target="_blank">Turn Up The Volume Blog</a></li>
                        <li><a href="https://monobrowdemos.wordpress.com/" target="_blank">Oasis Demo Info</a></li>
                        <li><a href="http://live4ever.proboards.com/" target="_blank">Live4ever Forum</a></li>
                        <li><a href="https://www.reddit.com/r/oasis" target="_blank">Oasis sub-Reddit</a></li>
                    </ul>
                </div>
            </div>
        );
    }
}

export default Credits;