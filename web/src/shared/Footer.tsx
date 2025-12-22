/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";
import { Link } from "react-router";
import MediaQuery from "react-responsive";

export default class Footer extends React.Component<any, any> {

    render() {
        return(
            <div id="footer" className="tableCaptionBottom" data-testid="footer-test">
                <footer>
                    <MediaQuery minDeviceWidth={768}>
                        <div data-testid="footer-social-follow">
                            <span>Follow On</span>
                            <div>
                                <span>
                                <a href="https://bsky.app/profile/oasistimeline.bsky.social" target="_blank"
                                   rel="noopener noreferrer" >
                                    <i className="fa-brands fa-bluesky socialIcon" data-testid="bluesky-follow" title="Follow on Bluesky"></i>
                                </a>

                                <a href="https://twitter.com/OasisTimeline" target="_blank" rel="noopener noreferrer">
                                    <i className="fa-brands fa-twitter socialIcon" data-testid="twitter-follow" title="Follow on Twitter"></i>
                                </a>
                                </span>
                            </div>
                        </div>
                        <div data-testid="footer-copyright-test">
                        <span>&copy; 2026 / <Link to="/about">About</Link> / <Link to="/terms">Terms</Link> / <a href="https://github.com/jdpgrailsdev/oasis-timeline" target="_blank" rel="noopener noreferrer">GitHub</a></span>
                        </div>
                        <div data-testid="footer-last-updated-test">
                            <span>Last Updated: {process.env.REACT_APP_UPDATED_AT}</span>
                        </div>
                    </MediaQuery>
                    <MediaQuery maxDeviceWidth={767}>
                        <div data-testid="footer-social-follow">
                            <span>Follow On</span>
                            <div>
                                <span>
                                <a href="https://bsky.app/profile/oasistimeline.bsky.social" target="_blank"
                                   rel="noopener noreferrer">
                                    <i className="fa-brands fa-bluesky socialIcon" data-testid="bluesky-follow"
                                       title="Follow on Bluesky"></i>
                                </a>

                                <a href="https://twitter.com/OasisTimeline" target="_blank" rel="noopener noreferrer">
                                    <i className="fa-brands fa-twitter socialIcon" data-testid="twitter-follow"
                                       title="Follow on Twitter"></i>
                                </a>
                                </span>
                            </div>
                        </div>
                        <span data-testid="footer-copyright-test">&copy; 2026 / <Link to="/about">About</Link> / <Link
                            to="/terms">Terms</Link> / <a href="https://github.com/jdpgrailsdev/oasis-timeline"
                                                          target="_blank" rel="noopener noreferrer">GitHub</a></span>
                        <span data-testid="footer-last-updated-test">Last Updated</span>
                        <span data-testid="footer-last-updated-timestamp-test">{process.env.REACT_APP_UPDATED_AT}</span>
                    </MediaQuery>
                </footer>
            </div>
        );
    }
}