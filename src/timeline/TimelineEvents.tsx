import * as React from "react";
import { TimelineEvent } from "react-event-timeline";
import TimelineData from '../data/timelineData.js';
import AdditionalContextData from '../data/additionalContextData.js';
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
        const key = AdditionalContextData.generateKey(timestamp, type) + "_" + "_modal";
        if(AdditionalContextData.hasAdditionalContext(timestamp, type)) {
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
        var visible = events.filter(e => e.props.style.display == 'block').length
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
        const key = AdditionalContextData.generateKey(timestamp, type) + "_" + "_modal";
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
                {event.description}
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