// https://www.npmjs.com/package/react-event-timeline
// https://google.github.io/material-design-icons/

import * as React from 'react';
import * as ReactDOM from "react-dom";
import Timeline from "./Timeline";

class App extends React.Component<any, any> {

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
    const name = target.name;

    this.setState({
      [name]: value
    });
  }

  render() {
      return(
        <div>
        <h1>Oasis Timeline</h1>
        <div className="filter">Filter</div>
        <form>
          <label>
              Recordings
              <input name="Recordings" type="checkbox" checked={this.state.recordings} onChange={this.handleInputChange}/>
          </label>
          <label>
              Releases
              <input name="Releases" type="checkbox" checked={this.state.releases} onChange={this.handleInputChange}/>
          </label>
          <label>
              Gigs
              <input name="Gigs" type="checkbox" checked={this.state.gigs} onChange={this.handleInputChange}/>
          </label>
          <label>
              Noteworthy Events
              <input name="Noteworthy" type="checkbox" checked={this.state.noteworthy} onChange={this.handleInputChange}/>
          </label>
        </form>
        <br />
        <div>
          <Timeline />
        </div>
      </div>
    );
  }
}

ReactDOM.render(
  <App />,
  document.getElementById('root')
);