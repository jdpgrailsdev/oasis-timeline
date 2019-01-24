import * as React from "react";
import * as ReactDOM from "react-dom";
import { Timeline, TimelineEvent } from "react-event-timeline";
import TimelineEvents from "./TimelineEvents";

export default class FilterableTimeline extends React.Component<any, any> {

  constructor(props: any) {
    super(props);
    this.state = {
      recordings: true,
      releases: true,
      gigs: true,
      noteworthy: true
    };

    this.handleInputChange = this.handleInputChange.bind(this);
  }

  handleInputChange(event: any) {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name.toLowerCase();

    this.setState({
      [name]: value
    });

    this.filterTimeline(name, value);
  }

  filterTimeline(name: any, value: any) {
    const node = ReactDOM.findDOMNode(this);
    if (node instanceof HTMLElement) {
        Array.from(node.querySelectorAll('.' + name)).forEach(
            (element, index, array) => {
                if(element instanceof HTMLElement) {
                    if(value == true) {
                        element.style.display = 'block';
                    } else {
                        element.style.display = 'none';
                    }
                }
            }
        );
    }
  }

    render() {
      return(
<div>
    <form className="filterForm">
        <div className="filter">Filter
              <label>
                  Recordings
                  <input name="Recordings" type="checkbox" defaultChecked={this.state.recordings} onChange={this.handleInputChange}/>
              </label>
              <label>
                  Releases
                  <input name="Releases" type="checkbox" defaultChecked={this.state.releases} onChange={this.handleInputChange}/>
              </label>
              <label>
                  Important Gigs
                  <input name="Gigs" type="checkbox" defaultChecked={this.state.gigs} onChange={this.handleInputChange}/>
              </label>
              <label>
                  Noteworthy Events
                  <input name="Noteworthy" type="checkbox" defaultChecked={this.state.noteworthy} onChange={this.handleInputChange}/>
              </label>
        </div>
    </form>
    <br />
    <div>
        <Timeline style={{ width: '65%' }}>
            <TimelineEvents />
        </Timeline>
    </div>
</div>
          );
    }
}