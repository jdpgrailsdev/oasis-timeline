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
import SourceList from "./shared/SourceList";
import {Link} from "react-router-dom";

export default class Sources extends React.Component<any, any> {

    render() {
        return(
            <div className="main" id="top" data-testid="sources-top-test">
                <h2>Sources</h2>
                <div className="mainText">
                    The following sites have been used for source information and/or contributed information to the Oasis Timeline project:
                    <SourceList />
                </div>
                <h3>Special Thanks</h3>
                <div className="mainText">
                    A very special thank you to the following sites and people:
                    <ul>
                        <li><a href="https://www.microdotshop.co.uk/" target="_blank" rel="noopener noreferrer">Brian Cannon/Microdot Creative</a></li>
                        <li><a href="http://www.nigeldick.com/" target="_blank" rel="noopener noreferrer">Nigel Dick</a></li>
                        <li><a href="http://www.oasisinet.com/#!/gigs/list" target="_blank" rel="noopener noreferrer">Official Oasis Gigography</a></li>
                        <li><a href="http://www.oasis-recordinginfo.co.uk/" target="_blank" rel="noopener noreferrer">Oasis Recording Information</a></li>
                        <li><a href="http://www.oocities.org/sunsetstrip/underground/3284/contents.html" target="_blank" rel="noopener noreferrer">Andrew Turner's Oasis FAQ</a></li>
                        <li><a href="https://turnupthevolume.blog" target="_blank" rel="noopener noreferrer">Turn Up The Volume Blog</a></li>
                        <li><a href="https://monobrowdemos.wordpress.com/" target="_blank" rel="noopener noreferrer">Oasis Demo Info</a></li>
                        <li><a href="http://live4ever.proboards.com/" target="_blank" rel="noopener noreferrer">Live4ever Forum</a></li>
                        <li><a href="https://www.reddit.com/r/oasis" target="_blank" rel="noopener noreferrer">Oasis sub-Reddit</a></li>
                        <li><a href="https://en.wikipedia.org/wiki/Oasis_(band)" target="_blank" rel="noopener noreferrer">Oasis Wikipedia Article(s)</a></li>
                        <li><a href="http://www.feelnumb.com/2010/10/13/girls-in-photos-oasis-cigarettes-alcohol-and-wonderwall-single-cover/" target="_blank" rel="noopener noreferrer">feelnumb - Girls In Photos Of Oasis “Cigarettes & Alcohol” And “Wonderwall” Single Cover</a></li>
                        <li><a href="https://www.ukcia.org/potculture/91/oasis2.html" target="_blank" rel="noopener noreferrer">Pot Culture: Liam Gallagher's exploits in 1996</a></li>
                        <li><a href="https://groups.google.com/forum/#!topic/alt.music.oasis/Is3w0VS6sbM" target="_blank" rel="noopener noreferrer">The Oasis Diaries - Q Magazine, September 1997</a></li>
                        <li><a href="http://oasisinterviews.blogspot.com/" target="_blank" rel="noopener noreferrer">Oasis Interviews Archive</a></li>
                        <li><a href="https://www.mdmarchive.co.uk/artefact/19648/OASIS_ACADEMY_3_(HOP__GRAPE)_TICKET_1993" target="_blank" rel="noopener noreferrer">Oasis Supporting Dodgy at Hop & Grape, Manchester University, June 18, 1993</a></li>
                        <li><a href="https://louderthanwar.com/john-robb-was-the-first-person-to-hear-oasis-whats-the-story-it-ended-with-shotguns-a-kangeroo-court-and-madness/" target="_blank" rel="noopener noreferrer">Louder Than War - John Robb was the first person to hear Oasis ‘What’s The Story…’ it ended with shotguns, a kangeroo court and madness</a></li>
                    </ul>
                </div>
                <h3>Disclaimer</h3>
                <div className="mainText">
                    The Oasis Timeline is not responsible for the content of source material and does not necessarily endorse the views expressed within.
                    For more information, please see the <Link to="/terms">terms and conditions</Link> for this site.
                </div>
                <br />
                <br />
                <BackToTop baseUri="/sources" anchorId="top" />
                <br />
                <br />
                <br />
                <br />
            </div>
        );
    }
}