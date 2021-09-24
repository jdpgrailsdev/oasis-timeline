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
import HeatMap from "./shared/HeatMap";
import MediaQuery from "react-responsive";

const CELL_SIZE = 17;
const CELL_PADDING = 0.5;
const MOBILE_ROW_WIDTH_FACTOR = 3;      //e.g. 3 months of cells
const ROW_WIDTH_FACTOR = 6;             //e.g. 6 months of cells

// Month width = 7 days of cells plus one additional for padding
const MONTH_WIDTH = ((CELL_SIZE + CELL_PADDING) * 7) + CELL_SIZE;

export default class Home extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            selectedDate: new Date()
        }

        this.selectedDate = this.selectedDate.bind(this);
    }

    selectedDate(newState:any) {
        if(newState.selectedDate !== this.state.selectedDate) {
            this.setState(newState);
        }
    }

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
                            August 11, 1996
                        </cite>
                    </div>
                </div>
                <br />
                <div className={"mainText centered"}>
                    The history of <span className="oasis"><a href="http://www.oasisinet.com"  target="_blank" rel="noopener noreferrer">Oasis</a></span> as documented through <b>{TimelineData.getNumberOfEvents()}</b> events spanning <b>{TimelineData.getNumberOfYears()}</b> years.
                </div>
                <br />
                <MediaQuery minDeviceWidth={768}>
                    <br />
                    <HeatMap
                            callback={this.selectedDate}
                            cellSize={CELL_SIZE}
                            cellPadding={CELL_PADDING}
                            height={CELL_SIZE * 20}
                            monthsPerRow={ROW_WIDTH_FACTOR}
                            monthWidth={MONTH_WIDTH}
                            width={MONTH_WIDTH * (ROW_WIDTH_FACTOR + 0.2)} />
                    <br />
                </MediaQuery>
                <MediaQuery maxDeviceWidth={767}>
                    <br />
                    <HeatMap
                        callback={this.selectedDate}
                        cellSize={CELL_SIZE}
                        cellPadding={CELL_PADDING}
                        height={CELL_SIZE * 37}
                        monthsPerRow={MOBILE_ROW_WIDTH_FACTOR}
                        monthWidth={MONTH_WIDTH}
                        width={MONTH_WIDTH * (MOBILE_ROW_WIDTH_FACTOR + 0.2)} />
                    <br />
                </MediaQuery>
                <TodayInHistory selectedDate={this.state.selectedDate} />
                <br />
                <br />
                <BackToTop baseUri="/" anchorId="top" />
                <br />
                <br />
            </div>
        );
    }
}
