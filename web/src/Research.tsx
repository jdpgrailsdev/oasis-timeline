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
import BackToTop from "./shared/BackToTop";
import Spacer from "./shared/Spacer";
import {Link} from "react-router";
import ResearchDataLoader from './data/researchDataLoader.js';

export default class Research extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            research: ResearchDataLoader.data
        };
    }

    render() {

        return(
            <div className="main" id="top" data-testid="research-top-test">
                <h2>Research</h2>
                <div className="mainText">
                    This page documents in progress research for events that have not yet been verified.  The veracity of the information
                    on this page should be assumed to be questionable.  As events are verified, they will be moved from this page to
                    the timeline.  Please contact us if you have any information that would help verify the information listed on this
                    page.
                </div>
                <br />
                <Spacer />
                <br />
                <div className="mainText">Click on a title to expand to see the current research for that event.</div>
                <br />
                { [...this.state.research].map(d => ResearchDataLoader.generateResearchData(d)) }
                <br />
                <Spacer />
                <br />
                <div className="mainText">If you have any information that may help verify the exact date for any of the events above, or if you have information about an event
                that you believe should be included on the timeline, please head over to the <Link to="/contributing#top">contributing section</Link> of this site to learn
                how you may help!</div>
                <br />
                <BackToTop baseUri="/research" anchorId="top" />
                <br />
                <br />
            </div>
        );
    }
}