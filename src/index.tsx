// https://www.npmjs.com/package/react-event-timeline
// https://google.github.io/material-design-icons/
// https://reactjsexample.com/a-simple-react-timeline-component-for-filtering-data/

import * as React from 'react';
import * as ReactDOM from "react-dom";
import Timeline from "./Timeline";
import { reveal as Menu } from 'react-burger-menu';

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

  showSettings(event: any) {
    event.preventDefault();
  }

  render() {
      return(
        <div id="outer-container">
          <Menu pageWrapId={ "page-wrap" } outerContainerId={ "outer-container" }>
              <a id="home" className="menu-item" href="%PUBLIC_URL%">
                  <i className="material-icons">home</i>
                  <span>Home</span>
              </a>
              <a id="timeline" className="menu-item" href="%PUBLIC_URL%">
                  <i className="material-icons">timeline</i>
                  <span>Timeline</span>
              </a>
              <a id="comments" className="menu-item" href="https://github.com/jdpgrailsdev/oasis-timeline/issues" target="_blank">
                  <i className="material-icons">chat_bubble_outline</i>
                  <span>Comments</span>
              </a>
          </Menu>
          <main id="page-wrap">
            <div>
                <h1><img src="images/oasis-logo.jpg" alt="oasis" className="logo" /> Timeline</h1>
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
                  <Timeline />
                </div>
              </div>
          </main>
        </div>
    );
  }
}

ReactDOM.render(
  <App />,
  document.getElementById('root')
);