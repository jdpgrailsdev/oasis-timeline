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
import { HashLink as Link } from "react-router-hash-link";
import MediaQuery from "react-responsive";
import jump from 'jump.js';
import inView from 'in-view';
import TimelineData from '../data/timelineDataLoader.js';
import YearScroll from '../shared/YearScroll';

export default class TimelineNavigation extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            autoJumpEnabled: true,
            navigationYear: TimelineData.getFirstYear(),
            _isMounted: false
        };

        this.jumpToYear = this.jumpToYear.bind(this);
        this.setNavigationYear = this.setNavigationYear.bind(this);
        this.toggleAutoJumpEnabled = this.toggleAutoJumpEnabled.bind(this);
    }

    componentDidMount() {
        inView.offset(50);
        inView('[id^=year_]')
            .on('enter', (el:any) => {
                if(this.state.autoJumpEnabled) {
                    this.setNavigationYear(el.id.split("_").pop());
                }
            });
        this.setState({_isMounted: true })
    }

    componentWillUnmount() {
        this.setState({_isMounted: false })
    }

    generateActiveNavigation() {
        if(this.props.navigationActive) {
            return this.generateNavigation();
        } else {
            return <div></div>;
        }
    }

    generateNavigation() {
        let menu = [];
        menu.push(<i key="navigation_icon" className="material-icons md-14">navigation</i>);
        menu.push(<span key="navigation_label" className="navigationLabel">NAVIGATION</span>);
        menu.push(<div key="navigation_year_jump_label" className="yearJump"><i className="material-icons md-14 yearScrollIcon">today</i><YearScroll selectedYear={this.state.navigationYear} onChange={this.jumpToYear} /></div>);
        menu.push(<div key="navigation_back_label" className="navigationBack"><i className="material-icons md-14">arrow_upward</i><Link to="/timeline#top">Back To Top</Link></div>);
        menu.push(<div key="navigation_jump_label" className="navigationJump"><i className="material-icons md-14">arrow_downward</i><Link to="/timeline#bottom">Jump To End</Link></div>);
        return menu;
    }

    jumpToYear(year: any) {
        if(year !== null) {
            if(TimelineData.hasEventsForYear(year)) {
                this.toggleAutoJumpEnabled(() => {
                    jump("[id='year_" + year + "']", {
                        callback: () => {
                            if(this.state._isMounted) {
                                this.setNavigationYear(year);
                                this.toggleAutoJumpEnabled(() => {});
                            }
                        }
                    });
                });
            } else {
                this.setNavigationYear(year);
            }
        }
    }

    setNavigationYear(selectedYear:any) {
        if(this.state._isMounted) {
            if(selectedYear !== this.state.navigationYear) {
                this.setState({navigationYear: selectedYear});
            }
        }
    }

    toggleAutoJumpEnabled(callback:any) {
        this.setState((prevState:any) => ({
              autoJumpEnabled: !prevState.autoJumpEnabled
        }), callback);
    }

    render() {
        return(
        <div className="navigation">
            <MediaQuery minDeviceWidth={768}>
                { this.generateNavigation() }
            </MediaQuery>
            <MediaQuery maxDeviceWidth={767}>
                { this.generateActiveNavigation() }
            </MediaQuery>
        </div>);
    }
}