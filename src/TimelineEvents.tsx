import * as React from "react";
import { TimelineEvent } from "react-event-timeline";
import TimelineData from './data/timelineData.js';

export default class TimelineEvents extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            timelineEvents: TimelineData.data
        };
    }

    createTimelineEvents = () => {
        return [...this.state.timelineEvents]
            .map((event, i) => this.generateTimelineEvent(event, i));
    }

    generateTimelineEvent(event: any, i:any) {
        const className = "timeline_event type_" + event.type + " year_" + event.year;
        const timestamp = event.date + ", " + event.year;

        return <div className={className} key={event.type + "_" + i}>
                <TimelineEvent
                    title={event.title}
                    createdAt={timestamp}
                    icon={ <i className="material-icons md-18">{TimelineData.getIcon(event.type)}</i> }
                    contentStyle={{ fontFamily: 'Roboto' }}
                >
                {event.description}
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