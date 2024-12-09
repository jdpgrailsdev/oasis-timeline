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
import { HashLink as Link } from "react-router-hash-link";
import Disqus from 'disqus-react';
import BackToTop from "./shared/BackToTop";
import TimelineData from './data/timelineDataLoader.js';
import SourceUtils from './util/sourceUtils.js';

export default class Contributing extends React.Component<any, any> {

    getMissingSources() {
        let missing = [...TimelineData.data]
            .filter(event => SourceUtils.isDisputed(event) === true )
            .map((event,i) => <li data-testid="disputed-source-test" key={"missing_" + i}>{event.title}</li>);
        return missing;
    }

    render() {
        const disqusShortname = 'oasis-timeline';
        const disqusConfig = {
            url: 'https://www.oasis-timeline.com/#/contributing',
            identifier: 'contributing',
            title: 'Contributing'
        };

        return(
            <div className="main" id="top" data-testid="contributing-top-test">
                <h2>Contributing</h2>
                <div className="mainText">
                    The goal of this site is to be as accurate as possible.  If you have information that may improve the timeline or have an event that you believe should be included in
                    the timeline, please do not hesitate to contact us!  The best place to do so is to add a comment to the <a href="http://live4ever.proboards.com/thread/90673/oasis-timeline-project" target="_blank" rel="noopener noreferrer">Oasis Timeline thread</a> on the <a href="http://live4ever.proboards.com/" target="_blank" rel="noopener noreferrer">Live4ever Forum</a>.
                    You may also make a comment <Link to="/contributing#disqus_thread">below</Link> via <a href="https://disqus.com" target="_blank" rel="noopener noreferrer">Disqus</a>, <a href="https://twitter.com/intent/tweet?screen_name=OasisTimeline&ref_src=twsrc%5Etfw" className="twitter-mention-button" data-show-count="false" target="_blank" rel="noopener noreferrer">Tweet to @OasisTimeline</a><script async src="https://platform.twitter.com/widgets.js"></script> or <a href="https://bsky.app/profile/oasistimeline.bsky.social" data-show-count="false" target="_blank" rel="noopener noreferrer">mention oasistimeline.bsky.social on Bluesky</a>.
                    <br/>
                    <br/>
                    For details about ongoing research into events with unverified dates, please see the <Link
                        to="/research#top">research section</Link> of this site.  This section
                    contains any and all source information gathered so far in an effort to pinpoint the exact date of events that have not yet been verified.
                    <br />
                    <br />
                    Likewise, if you find an issue with the site itself (bug, typo or other problem), please create an issue over at the <a href="https://github.com/jdpgrailsdev/oasis-timeline/issues" target="_blank" rel="noopener noreferrer">site's repository in GitHub.</a>
                </div>
                <h3>Want List</h3>
                <div className="mainText" data-testid="contributing-missing-sources-test">
                    Below are a list of events that are currently in need of confirmation/sources:
                    <ul>
                        <li>Filming date for the <i>Shakermaker</i> music video</li>
                        <li>Filming date for the <i>Morning Glory</i> music video</li>
                        <li>Filming date for the <i>Stand By Me</i> music video</li>
                        <li>Filming date for the <i>Stand By Me</i> - Live at Bonehead's music video</li>
                        <li>Filming date for the <i>All Around the World</i> music video</li>
                        <li>Date of when Noel Gallagher moved to London (believed to be early 1994)</li>
                        <li>Date of when Paul "Guigsy" McGuigan moved to London</li>
                        <li>Recording dates for all albums after <i>Be Here Now</i></li>
                        {this.getMissingSources()}
                    </ul>
                </div>
                <br />
                <br />
                <BackToTop baseUri="/contributing" anchorId="top" />
                <br />
                <br />
                <div className="contribute">
                    <Disqus.DiscussionEmbed shortname={disqusShortname} config={disqusConfig} />
                </div>
                <br />
                <br />
                <br />
                <br />
            </div>
        );
    }
}