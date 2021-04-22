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
import TimelineData from '../data/timelineDataLoader.js';

export default class YearScroll extends React.Component<any, any> {

    constructor(props: any) {
        super(props);

        this.scrollYearLeft = this.scrollYearLeft.bind(this);
        this.scrollYearRight = this.scrollYearRight.bind(this);
    }

    getCurrentlySelectedYear() {
        return this.props.selectedYear;
    }

    scrollYearLeft() {
        let nextYear = parseInt(this.getCurrentlySelectedYear()) - 1;
        if(nextYear < TimelineData.getFirstYear()) {
            nextYear = TimelineData.getLastYear();
        }
        this.props.onChange(nextYear);
    }

    scrollYearRight(event:any) {
        let nextYear = parseInt(this.getCurrentlySelectedYear()) + 1;
        if(nextYear > TimelineData.getLastYear()) {
            nextYear = TimelineData.getFirstYear();
        }
        this.props.onChange(nextYear);
    }

    render() {
        return(
            <div className="yearScroll" data-testid="year-scroll-test-top">
                <button className="yearScrollButton" type="button" onClick={this.scrollYearLeft}>
                    <i key="year_scroll_left" className="material-icons md-14">chevron_left</i>
                </button>
                <div>{this.getCurrentlySelectedYear()}</div>
                <button className="yearScrollButton" type="button" onClick={this.scrollYearRight}>
                    <i key="year_scroll_left" className="material-icons md-14">chevron_right</i>
                </button>
            </div>
        );
    }

}