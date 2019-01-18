import * as React from "react";

class Home extends React.Component<any, any> {

    render() {
        return(
            <div className="main">
                <h2>Welcome</h2>
                <div className="mainText">
                This site strives to be the canonical, historical timeline for the British band <a href="https://www.oasisinet.com" target="_blank">Oasis</a>.  It is
                a work in progress and will continue to be updated as new information comes to light to help pinpoint key events in the career of Oasis.  The <a href="/timeline">timeline</a> maintained by this site has been compiled from various sources to date, including (but not limited to):
                <ul>
                    <li>Officially released material</li>
                    <li>Interviews</li>
                    <li>Biographies and other books about the band</li>
                    <li>Fan sites (such as <a href="http://www.oasis-recordinginfo.co.uk/" target="_blank">Oasis Recording Information</a> and <a href="https://monobrowdemos.wordpress.com/" target="_blank">Oasis Demo Info</a>)</li>
                    <li>Message boards (such as the <a href="http://live4ever.proboards.com/" target="_blank">Live4ever Forum</a> and the <a href="https://www.reddit.com/r/oasis" target="_blank">Oasis sub-Reddit</a>)</li>
                </ul>

                <p>
                <b>N.B.</b> Oasis Timeline is an unofficial fan website and is not related to Oasis, Sony Music, or Ignition Management.
                </p>
                </div>
            </div>
        );
    }
}

export default Home;