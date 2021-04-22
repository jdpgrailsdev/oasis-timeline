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
import TimelineData from './data/timelineDataLoader.js';
import TodayInHistory from "./shared/TodayInHistory";

export default class Home extends React.Component<any, any> {

    render() {
        return(
            <div className="main" id="top" data-testid="home-top-test">
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