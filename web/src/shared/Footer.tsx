import * as React from "react";
import { Link } from "react-router-dom";
import MediaQuery from "react-responsive";

export default class Footer extends React.Component<any, any> {

    render() {
        return(
            <div id="footer" className="tableCaptionBottom">
                <footer>
                    <MediaQuery query="(min-device-width: 768px)">
                        <div>
                            <span>&copy; 2020 / <Link to="/about">About</Link> / <Link to="/terms">Terms</Link> / <a href="https://github.com/jdpgrailsdev/oasis-timeline" target="_blank" rel="noopener noreferrer">GitHub</a> / <a href="https://twitter.com/OasisTimeline" target="_blank" rel="noopener noreferrer">Twitter</a></span>
                        </div>
                        <div>
                            <span>Last Updated: {process.env.REACT_APP_UPDATED_AT}</span>
                        </div>
                    </MediaQuery>
                    <MediaQuery query="(max-device-width: 767px)">
                        <span>&copy; 2020 / <Link to="/about">About</Link> / <Link to="/terms">Terms</Link> / <a href="https://github.com/jdpgrailsdev/oasis-timeline" target="_blank" rel="noopener noreferrer">GitHub</a> / <a href="https://twitter.com/OasisTimeline" target="_blank" rel="noopener noreferrer">Twitter</a></span>
                        <span>Last Updated</span>
                        <span>{process.env.REACT_APP_UPDATED_AT}</span>
                    </MediaQuery>
                </footer>
            </div>
        );
    }
}