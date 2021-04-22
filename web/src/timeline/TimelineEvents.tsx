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
import { TimelineEvent } from "react-event-timeline";
import TimelineData from '../data/timelineDataLoader.js';
import SourceUtils from '../util/sourceUtils.js';
import Modal from "../shared/Modal";

export default class TimelineEvents extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            timelineEvents: TimelineData.data
        };
    }

    additionalContext(timestamp:any, type:any) {
        const key = TimelineData.generateKey(timestamp, type) + "_modal";
        if(TimelineData.hasAdditionalContext(timestamp, type)) {
            return <span className="additionalContext">
                    <i className="material-icons md-12" style={{ cursor: 'pointer' }} onClick={e => { this.showModal(timestamp, type); }}>info</i>
                    <Modal timestamp={timestamp} type={type} show={this.state[key]} />
                   </span>;
        } else {
            return null;
        }
    }

    createTimelineEvents = () => {
        const events = [...this.state.timelineEvents]
            .map((event, i) => this.generateTimelineEvent(event, i));
        var visible = events.filter(e => e.props.style.display === 'block').length
        this.props.onChange({visibleEvents:visible});
        return events;
    }

    includeByFilter(filter:any) {
        if(filter == null) {
            return true;
        } else {
            return (filter != null && filter.checked);
        }
    }

    showModal(timestamp:any, type:any) {
        const key = TimelineData.generateKey(timestamp, type) + "_modal";
        this.setState({
            [key]: !this.state[key]
        });
      }

    generateTimelineEvent(event: any, i:any) {
        const className = "timeline_event type_" + event.type + " year_" + event.year;
        const timestamp = event.date + ", " + event.year;
        const color = SourceUtils.isDisputed(event) ? 'red' : 'black';
        const filter = this.props.filters.find((f:any) => event.type != null && event.type.endsWith(f.name));
        const includeEvent = this.includeByFilter(filter);
        const displayStyle = {
            display: includeEvent ? 'block' : 'none'
        };

        return <div className={className} style={displayStyle} id={"year_" + event.year} key={event.type + "_" + i}>
                <TimelineEvent
                    title={event.title}
                    createdAt={timestamp}
                    icon={ <i className="material-icons md-18">{TimelineData.getIcon(event.type)}</i> }
                    iconColor={color}
                    contentStyle={{ fontFamily: 'Roboto' }}
                    style={{ color: color }}
                >
                { TimelineData.descriptionToHTML(event) }
                { SourceUtils.generateSourceLink(event) }
                { this.additionalContext(timestamp, event.type) }
                </TimelineEvent>

            </div>;
    }

    render() {
      return(
          <div>
              {this.createTimelineEvents()}
          </div>
      )
    }
}