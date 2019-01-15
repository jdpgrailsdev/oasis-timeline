import * as React from "react";
import { Timeline, TimelineEvent } from "react-event-timeline";

export default () => {
  return (
    <Timeline>
      <div className="recordings">
          <TimelineEvent
            title="Mark Coyle recording session, Mark Coyle Home Studio, Manchester, UK"
            createdAt="Late 1992/Early 1993"
            icon={<i className="material-icons md-18">settings_voice</i>}
          >
            Early recording session at Mark Coyle's home studio on Mauldeth Road West in Manchester, UK over
            various dates from late 1992 to early 1993.  Songs recorded in these sessions include "D'Yer Wanna Be a Spaceman?", "Going Nowhere", "Hello", "Married With Children", "Rockin' Chair" and "She's Electric"
          </TimelineEvent>
      </div>
      <div className="recordings">
          <TimelineEvent
            title="Real People Recording Session, Liverpool, UK"
            createdAt="Spring 1993"
            icon={<i className="material-icons md-18">settings_voice</i>}
          >
            Recording session at the Porter Street Studio in Liverpool, UK that produced the "<a href="https://en.wikipedia.org/wiki/Live_Demonstration" target="_blank">Live Demonstration"</a> tape.
          </TimelineEvent>
      </div>
    </Timeline>
  );
};
