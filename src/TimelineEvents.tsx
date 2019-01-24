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
        let events = []

        for(var i in this.state.timelineEvents) {
            const event = this.state.timelineEvents[i];
            const className = event.type + " " + event.year;
            const timestamp = event.date + ", " + event.year;

            events.push(
<div className={className} key={event.type + i}>
    <TimelineEvent
        title={event.title}
        createdAt={timestamp}
        icon={ <i className="material-icons md-18">{TimelineData.getIcon(event.type)}</i> }
        contentStyle={{ fontFamily: 'Roboto' }}
    >
        {event.description}
    </TimelineEvent>
</div>);
        }

        return events;
    }

    render() {
      return(
          <div>
              {this.createTimelineEvents()}
          </div>
      )
    }
}