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
import Collapsible from "./shared/Collapsible";
import BackToTop from "./shared/BackToTop";
import Spacer from "./shared/Spacer";
import {Link} from "react-router-dom";

export default class Research extends React.Component<any, any> {

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
                <Collapsible title="Swirl Logo Creation">
                    <br />
                    <div>Research</div>
                    <ul>
                        <li>The original Oasis Union Jack swirl logo is created by Tony French some time in the Spring of 1993.</li>
                        <li>The logo is commissioned by Noel Gallagher for use on the <i>Live Demonstration</i> tape, which contains recordings
                    from the the session at Porter Street Studio in Liverpool, UK with the Real People, also in the Spring of 1993.</li>
                        <li>This all occurs prior to the famous King Tuts gig on May 31, 1993.</li>
                    </ul>
                    <div>Sources</div>
                    <ul>
                        <li><a href="https://www.youtube.com/watch?v=y5gBUJEGHMw&t=77s" target="_blank" rel="noopener noreferrer">https://www.youtube.com/watch?v=y5gBUJEGHMw&t=77s</a></li>
                    </ul>
                </Collapsible>
                <br />
                <Collapsible title="Definitely Maybe Monnow Valley Recording Session">
                    <br />
                    <div>Research</div>
                    <ul>
                        <li>The band had gigs all the way up through December 16, 1993.</li>
                        <li>Tony McCarroll states in his book that they were at Monnow Valley after they heard Columbia on the radio for the first time.  The white label demo was aired on December 6, 1993 on BBC Radio One.</li>
                        <li>He also states that the session lasted 18 days.</li>
                        <li>The band recorded Supersonic at The Pink Museum in Liverpool on December 19, 1993, so they wouldn't have been at Monnow Valley at that time.</li>
                        <li>The band recorded a session for BBC Radio One on December 22, 1993 at Maida Vale Studios which was later broadcast on January 4th 1994.</li>
                        <li>Liam Gallagher is on vacation in Tenerife, Spain from December 26th - 28th, 1993.</li>
                        <li>The cover for Supersonic single was shot at Monnow Valley Studios on January 26, 1994.</li>
                        <li>The band played their famous gig at Water Rats in London on January 27.</li>
                        <li>Tentative dates for the session are January 7, 1994 to January 26, 1994.</li>
                    </ul>
                    <div>Sources</div>
                    <ul>
                        <li><a href="https://johnblakebooks.com/oasis-the-truth.html" target="_blank" rel="noopener noreferrer">Oasis: The Truth</a></li>
                    </ul>
                </Collapsible>
                <br />
                <Collapsible title="Definitely Maybe Sawmill Studios Recording Session">
                    <br />
                    <div>Research</div>
                    <ul>
                        <li>The band played their famous gig at Water Rats in London on January 27.</li>
                        <li>The band didn't head to Sawmill Studios sometime until after the Water Rates gig.  Tony McCarroll states in his book that they went from London back to Manchester for one day and then down to Sawmill Studios.</li>
                        <li>Paolo Hewitt states in Getting High that the band went to Olympic Studios to mix the Monnow Valley sessions for a couple days before they realized it wasn't going to work.</li>
                        <li>Anjali Dutt (engineer on the sessions) stated that the Sawmill Studios session didn't start until after the Gleneagles gig (February 6th, 1994) AND the Amsterdam ferry incident (February 17th, 1994).</li>
                        <li>Oasis resuming touring on March 23rd, 1994 in Bedford, UK.  Therefore, the sessions most likely ran from some time after February 17th until some time in mid-March.</li>
                        <li>Tentative dates for the session are February 24, 1994 to March 4, 1994.</li>
                    </ul>
                    <div>Sources</div>
                    <ul>
                        <li><a href="http://www.oasis-recordinginfo.co.uk/?page_id=137" target="_blank" rel="noopener noreferrer">Oasis Recording Info: It’s Getting Better: The Sawmills Sessions</a></li>
                        <li><a href="https://deanstreetpress.co.uk/pages/book_page/19" target="_blank" rel="noopener noreferrer">Getting High: The Adventures of Oasis</a></li>
                        <li><a href="https://johnblakebooks.com/oasis-the-truth.html" target="_blank" rel="noopener noreferrer">Oasis: The Truth</a></li>
                    </ul>
                </Collapsible>
                <br />
                <Collapsible title="Shakermaker Video Shoot">
                    <br />
                    <div>Research</div>
                    <ul>
                        <li>The video is filmed at three distinct locations:  8 Stratford Avenue in Didsbury (Bonehead's old house), Fog Lane Park and obviously Sifter's Records, all of which are within a 2 mile radius of each other.</li>
                        <li>There may even be a few shots in front of Noel and Liam's childhood house on Cranwell Drive in neighboring Burnage.</li>
                        <li>Based on the flowers and grass visible during the shots in Bonehead's back garden, the video appears to have been filmed in the springtime or early summer, 1994.</li>
                        <li>A check of the band's touring schedule shows that they were on a break from May 15th to June 1, 1994.  They were also on a break from April 14th to April 28th, 1994, but based on the greenery in the video, I am guessing the May timeline makes more sense.</li>
                        <li>The cover to Definitely Maybe is photographed on May 27th at Bonehead's house, so the video shoot would have been on a different day and quite possibly around the same time for logistical reasons.</li>
                        <li>The video was released on June 13th.  As this was their second music video and prior to the release of Definitely Maybe, it most likely did not have a huge budget and was probably filmed in one day.</li>
                        <li>Likely filming date is some time during the last week of May, 1994</li>
                    </ul>
                    <div>Sources</div>
                </Collapsible>
                <br />
                <Collapsible title="Shakermaker Single Cover Shoot">
                    <br />
                    <div>Research</div>
                    <ul>
                        <li>Shot in Michael Spencer Jones' apartment on Clyde Road, West Didsbury, Manchester, UK in the Spring of 1994.</li>
                    </ul>
                    <div>Sources</div>
                    <ul>
                        <li><a href="https://www.youtube.com/watch?v=QnXfSJ1el0E" target="_blank" rel="noopener noreferrer">https://www.youtube.com/watch?v=QnXfSJ1el0E</a></li>
                    </ul>
                </Collapsible>
                <br />
                <Collapsible title="Supersonic (US) Video Shoot">
                    <br />
                    <div>Research</div>
                    <ul>
                        <li>Parts of the video were shot at the Cabazon Dinosaurs in Cabazon, CA (east of Lost Angeles near Palm Springs, CA).</li>
                        <li>As part of the US tour, Oasis arrived in Los Angeles, CA on either September 27th or 28th ahead of the show at the Whiskey A Go Go.</li>
                        <li>Epic Records throws a party for Oasis on the afternoon of the Whiskey A Go Go gig (September 29th, 1994).</li>
                        <li>Oasis played there ill-fated concert at the Whiskey A Go Go on September 29, 1994.</li>
                        <li>At some point during the tour, Gugisy sends his sister Mary a post card mentioning that the band just filmed the music video.  However, the only visible postcard has an October 6th postmark.</li>
                        <li>Noel Gallagher famously quits the band that night and spends at least 3 days in San Francisco with Melissa Lim.</li>
                        <li>Noel Gallagher goes to Las Vegas, NV with Tim Abbot of Creation Records for a few more days.</li>
                        <li>Oasis took part in a recording session at The Congress House in Austin, TX on October 8, 1994 after Noel rejoined the band.</li>
                        <li>Oasis US tour resumed in Minneapolis, MN on October 14th, 1994</li>
                        <li>The music video is released on November 15th, 1994.</li>
                    </ul>
                    <div>Sources</div>
                    <ul>
                        <li><a href="https://deanstreetpress.co.uk/pages/book_page/19" target="_blank" rel="noopener noreferrer">Getting High: The Adventures of Oasis</a></li>
                        <li><a href="https://www.sfchronicle.com/movies/article/Major-player-in-Oasis-story-emerges-in-wake-of-10630846.php?psid=cXpPr" target="_blank" rel="noopener noreferrer">Major player in Oasis story emerges in wake of 'Supersonic' doc</a></li>
                        <li><a href="http://www.oasis-recordinginfo.co.uk/?page_id=702" target="_blank" rel="noopener noreferrer">http://www.oasis-recordinginfo.co.uk/?page_id=702</a></li>
                        <li><a href="https://twitter.com/mary_mcguigan/status/1243667594453962753" target="_blank" rel="noopener noreferrer">https://twitter.com/mary_mcguigan/status/1243667594453962753</a></li>
                        <li><a href="https://eil.com/shop/moreinfo.asp?catalogid=284423" target="_blank" rel="noopener noreferrer">https://eil.com/shop/moreinfo.asp?catalogid=28442</a></li>
                    </ul>
                </Collapsible>
                <br />
                <Collapsible title="All Around the World Single Cover Shoot">
                    <br />
                    <div>Research</div>
                    <ul>
                        <li>Shot on a beach in Bournemouth, UK.</li>
                        <li>Coincided with a football match between Bournemouth and Wigan Athletic.  The match was played on August 16, 1997 (a Saturday).</li>
                        <li>According to Brian Cannon of Microdot Creative, the photo shoot would not have been in match day.</li>
                    </ul>
                    <div>Sources</div>
                    <ul>
                        <li><a href="https://twitter.com/microdotcreativ/status/1253420517383524364E" target="_blank" rel="noopener noreferrer">https://twitter.com/microdotcreativ/status/1253420517383524364</a></li>
                    </ul>
                </Collapsible>
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