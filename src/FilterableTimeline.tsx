import * as React from "react";
import * as ReactDOM from "react-dom";
import { Timeline, TimelineEvent } from "react-event-timeline";
import { CSSTransition, Transition, TransitionGroup } from "react-transition-group";
import { HashLink as Link } from "react-router-hash-link";
import MediaQuery from "react-responsive";
import Select from "react-select";
import Footer from "./Footer";
import TimelineEvents from "./TimelineEvents";
import TimelineData from './data/timelineData.js';
import TimelineFilters from "./TimelineFilters";
import TimelineNavigation from "./TimelineNavigation";

export default class FilterableTimeline extends React.Component<any, any> {

    render() {
      return(
        <div className="main" id="top">
            <div className="stickyPanel">
                <MediaQuery query="(min-device-width: 768px)">
                    <div className="menuSpacer"></div>
                    <TimelineNavigation parent={this} />
                    <div className="menuSpacer"></div>
                    <TimelineFilters parent={this} />
                    <div className="menuSpacer"></div>
                </MediaQuery>
                <MediaQuery query="(max-device-width: 767px)">
                    <TransitionGroup>
                        <CSSTransition classNames="" key="filter" timeout={500}>
                            <div>
                                <TimelineNavigation parent={this} />
                                <TimelineFilters parent={this} />
                            </div>
                        </CSSTransition>
                    </TransitionGroup>
                </MediaQuery>
            </div>
            <MediaQuery query="(min-device-width: 768px)">
                <br />
            </MediaQuery>
            <MediaQuery query="(max-device-width: 767px)">
                <br />
                <br />
                <br />
            </MediaQuery>
            <div className="timelinePanel">
                <h2>Timeline</h2>
                <span className="filterText">Showing { TimelineData.getNumberOfEvents() } of { TimelineData.getNumberOfEvents() } total events.</span>
                <br />
                <div className="disputed">
                    Events shown in <span>RED</span> are missing sources and/or the accuracy is in dispute.
                </div>
                <br />
                <Timeline style={{ width: '60%' }}>
                    <TimelineEvents />
                </Timeline>
                <br />
                <br />
                <Footer />
            </div>
        </div>);
    }
}