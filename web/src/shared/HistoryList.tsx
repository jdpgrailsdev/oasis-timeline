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
import SourceUtils from '../util/sourceUtils.js';

export default class HistoryList extends React.Component<any, any> {

    generateHtml(events: any) {
        return [...events].map((event, i) => this.generateLineItem(event, i));
    }

    generateLineItem(event: any, index: any) {
        let source = SourceUtils.generateSourceLink(event);
        let color = SourceUtils.isDisputed(event) ? 'red' : 'black';
        return <li style={{ color: color}} key={"event_today_" + index}><i className="material-icons">{TimelineData.getIcon(event.type)}</i><span className="historyItem"><b>In {event.year}:</b> {TimelineData.descriptionToHTML(event)}</span>{source}</li>;
    }

    render() {
        const today = this.props.selectedDate;
        const events = new Array<any>();
        const history = events.concat(TimelineData.generateHistory(today));

        if(history[0] !== undefined && history.length > 0) {
            return(
                <div className="left" data-testid="history-list-test">
                    <ul className="historyList">
                       {this.generateHtml(history)}
                    </ul>
                    <br />
                    <br />
                    <div>See the full <a href="#/timeline">timeline</a> for more events or <a href="https://twitter.com/OasisTimeline?ref_src=twsrc%5Etfw" className="twitter-follow-button" data-show-count="false" target="_blank" rel="noopener noreferrer">follow @OasisTimeline.</a></div>
                </div>);
        } else {
            return(
                <div data-testid="history-list-test">
                    <div>There are no events for {today}.</div>
                    <br />
                    <br />
                    <div>See the full <a href="#/timeline">timeline</a> for more events or <a href="https://twitter.com/OasisTimeline?ref_src=twsrc%5Etfw" className="twitter-follow-button" data-show-count="false" target="_blank" rel="noopener noreferrer">follow @OasisTimeline.</a></div>
                </div>);
        }
    }
}