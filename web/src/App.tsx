/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";
import {HashRouter, NavLink, Route} from "react-router-dom";
import MediaQuery from "react-responsive";
import {slide as Menu} from "react-burger-menu";
import Home from "./Home";
import About from "./About";
import Contributing from "./Contributing";
import Sources from "./Sources";
import Terms from "./Terms";
import FilterableTimeline from "./timeline/FilterableTimeline";
import Footer from "./shared/Footer";
import Research from "./Research";
import Banner from "./shared/Banner";

export default class App extends React.Component<any, any> {

    showSettings(event: any) {
        event.preventDefault();
    }

    render() {
        return(
            <HashRouter>
                <div id="outer-container" data-testid="app-top-test">
                    <MediaQuery maxDeviceWidth={767}>
                        <Menu pageWrapId={ "page-wrap" } outerContainerId={ "outer-container" } isOpen={ false }>
                            <NavLink to="/" className="menu-item" id="home" >
                                <i className="material-icons">home</i>
                                <span>Home</span>
                            </NavLink>
                            <NavLink to="/about" className="menu-item" id="about" >
                                <i className="material-icons">info</i>
                                <span>About</span>
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
                    </MediaQuery>
                    <main id="page-wrap" data-testid="app-main-test">
                        <MediaQuery minDeviceWidth={768}>
                            <div className="table">
                                <div id="header" className="tableCaption">
                                    <Banner />
                                </div>
                                <div id="menu" className="tableCaption menu">
                                    <div className="menu-item">
                                        <NavLink to="/" id="home">
                                            <i className="material-icons">home</i>
                                            <span>Home</span>
                                        </NavLink>
                                        <NavLink to="/about" id="about">
                                            <i className="material-icons">info</i>
                                            <span>About</span>
                                        </NavLink>
                                        <NavLink to="/contributing#disqus_thread" id="contributing">
                                            <i className="material-icons">chat_bubble_outline</i>
                                            <span>Contributing</span>
                                        </NavLink>
                                        <NavLink to="/sources" id="sources">
                                            <i className="material-icons">library_books</i>
                                            <span>Sources</span>
                                        </NavLink>
                                        <NavLink to="/timeline" id="timeline">
                                            <i className="material-icons">timeline</i>
                                            <span>Timeline</span>
                                        </NavLink>
                                    </div>
                                </div>
                                <div className="tableRow">
                                    <div id="body" className="content">
                                        <Route exact path="/" component={Home}/>
                                        <Route path="/about" component={About} />
                                        <Route path="/contributing" component={Contributing}/>
                                        <Route path="/research" component={Research}/>
                                        <Route path="/sources" component={Sources}/>
                                        <Route path="/terms" component={Terms}/>
                                        <Route path="/timeline" component={FilterableTimeline}/>
                                    </div>
                                </div>
                                <Footer />
                            </div>
                        </MediaQuery>
                        <MediaQuery maxDeviceWidth={767}>
                            <div className="table">
                                <div id="header" className="tableCaption">
                                    <Banner />
                                </div>
                                <div className="tableRow">
                                    <div className="tableRowGroup">
                                        <div id="body" className="content">
                                            <Route exact path="/" component={Home}/>
                                            <Route path="/about" component={About} />
                                            <Route path="/contributing" component={Contributing}/>
                                            <Route path="/research" component={Research}/>
                                            <Route path="/sources" component={Sources}/>
                                            <Route path="/terms" component={Terms}/>
                                            <Route path="/timeline" component={FilterableTimeline}/>
                                        </div>
                                    </div>
                                </div>
                                <Footer />
                            </div>
                        </MediaQuery>
                    </main>
                </div>
            </HashRouter>
        );
    }
}