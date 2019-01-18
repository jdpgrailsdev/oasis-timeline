// https://www.npmjs.com/package/react-event-timeline
// https://google.github.io/material-design-icons/
// https://reactjsexample.com/a-simple-react-timeline-component-for-filtering-data/
// https://www.npmjs.com/package/react-sticky

import * as React from 'react';
import * as ReactDOM from "react-dom";
import { Route, NavLink, HashRouter } from "react-router-dom";
import Home from "./Home";
import Comments from "./Comments";
import Timeline from "./Timeline";
import { reveal as Menu } from 'react-burger-menu';

class App extends React.Component<any, any> {

  showSettings(event: any) {
    event.preventDefault();
  }

  render() {
      return(
          <HashRouter>
            <div id="outer-container">
              <Menu pageWrapId={ "page-wrap" } outerContainerId={ "outer-container" }>
                <NavLink to="/" className="menu-item" id="home">
                    <i className="material-icons">home</i>
                    <span>Home</span>
                </NavLink>
                <NavLink to="/timeline" className="menu-item" id="timeline">
                    <i className="material-icons">timeline</i>
                    <span>Timeline</span>
                </NavLink>
                <NavLink to="/comments" className="menu-item" id="comments">
                    <i className="material-icons">chat_bubble_outline</i>
                    <span>Comments</span>
                </NavLink>
              </Menu>
              <main id="page-wrap">
                <div>
                    <h1><img src="images/oasis-logo.jpg" alt="oasis" className="logo" /> Timeline</h1>
                    <div className="content">
                        <Route exact path="/" component={Home}/>
                        <Route path="/timeline" component={Timeline}/>
                        <Route path="/comments" component={Comments}/>
                    </div>
                </div>
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
                <footer>
                    <span>&copy; 2019</span>
                    <span>Last Updated: Friday, January 19, 2019</span>
                </footer>
              </main>
            </div>
        </HashRouter>
    );
  }
}

ReactDOM.render(
  <App />,
  document.getElementById('root')
);