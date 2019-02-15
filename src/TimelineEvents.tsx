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
        const color = this.isDisputed(event) ? 'red' : 'black';

        return <div className={className} key={event.type + "_" + i}>
                <TimelineEvent
                    title={event.title}
                    createdAt={timestamp}
                    icon={ <i className="material-icons md-18">{TimelineData.getIcon(event.type)}</i> }
                    contentStyle={{ fontFamily: 'Roboto' }}
                    style={{ color: color}}
                >
                {event.description}
                { event.source ? <span className="sourceLink"><a href={event.source} target="_blank" rel="noopener noreferrer"><i className="material-icons md-12">library_books</i></a></span> : "" }
                </TimelineEvent>
            </div>;
    }

    isDisputed(event:any) {
        if(event.disputed) {
            return true;
        } else if(event.source) {
            return false;
        } else {
            return true;
        }
    }

    render() {
      return(
          <div>
              {this.createTimelineEvents()}
          </div>
      )
    }
}