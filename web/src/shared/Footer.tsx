import * as React from "react";
import { Link } from "react-router-dom";
import MediaQuery from "react-responsive";

export default class Footer extends React.Component<any, any> {

    render() {
        return(
            <div id="footer" className="tableCaptionBottom" data-testid="footer-test">
                <footer>
                    <MediaQuery minDeviceWidth={768}>
                        <div data-testid="footer-copyright-test">
                            <span>&copy; 2021 / <Link to="/about">About</Link> / <Link to="/terms">Terms</Link> / <a href="https://github.com/jdpgrailsdev/oasis-timeline" target="_blank" rel="noopener noreferrer">GitHub</a> / <a href="https://twitter.com/OasisTimeline" target="_blank" rel="noopener noreferrer">Twitter</a></span>
                        </div>
                        <div data-testid="footer-last-updated-test">
                            <span>Last Updated: {process.env.REACT_APP_UPDATED_AT}</span>
                        </div>
                    </MediaQuery>
                    <MediaQuery maxDeviceWidth={767}>
                        <span data-testid="footer-copyright-test">&copy; 2021 / <Link to="/about">About</Link> / <Link to="/terms">Terms</Link> / <a href="https://github.com/jdpgrailsdev/oasis-timeline" target="_blank" rel="noopener noreferrer">GitHub</a> / <a href="https://twitter.com/OasisTimeline" target="_blank" rel="noopener noreferrer">Twitter</a></span>
                        <span data-testid="footer-last-updated-test">Last Updated</span>
                        <span data-testid="footer-last-updated-timestamp-test">{process.env.REACT_APP_UPDATED_AT}</span>
                    </MediaQuery>
                </footer>
            </div>
        );
    }
}