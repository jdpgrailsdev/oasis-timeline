import * as React from 'react';
import * as ReactDOM from "react-dom";
import { Route, NavLink, HashRouter } from "react-router-dom";
import Home from "./Home";
import Contributing from "./Contributing";
import Sources from "./Sources";
import FilterableTimeline from "./FilterableTimeline";
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
                <NavLink to="/contributing#disqus_thread" className="menu-item" id="contributing">
                    <i className="material-icons">chat_bubble_outline</i>
                    <span>Contributing</span>
                </NavLink>
                <NavLink to="/sources" className="menu-item" id="sources">
                    <i className="material-icons">library_books</i>
                    <span>Sources</span>
                </NavLink>
                <NavLink to="/timeline" className="menu-item" id="timeline">
                    <i className="material-icons">timeline</i>
                    <span>Timeline</span>
                </NavLink>
              </Menu>
              <main id="page-wrap">
                <div>
                    <div id="header">
                        <h1><img src="images/oasis-logo.jpg" alt="oasis" className="logo" /> Timeline</h1>
                    </div>
                    <div id="body" className="content">
                        <Route exact path="/" component={Home}/>
                        <Route path="/contributing" component={Contributing}/>
                        <Route path="/sources" component={Sources}/>
                        <Route path="/timeline" component={FilterableTimeline}/>
                    </div>
                </div>
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