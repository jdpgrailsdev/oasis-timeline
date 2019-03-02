import * as React from "react";
import * as ReactDOM from "react-dom";
import { Timeline, TimelineEvent } from "react-event-timeline";
import { CSSTransition, Transition, TransitionGroup } from "react-transition-group";
import { HashLink as Link } from "react-router-hash-link";
import MediaQuery from "react-responsive";
import Select from "react-select";
import Footer from "../shared/Footer";
import TimelineEvents from "./TimelineEvents";
import TimelineData from '../data/timelineData.js';
import TimelineFilters from "./TimelineFilters";
import TimelineNavigation from "./TimelineNavigation";

export default class FilterableTimeline extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            filtersActive: false,
            filters: [
                { name: 'certifications', label: 'Certified Awards', checked: true },
                { name: 'gigs', label: 'Important Gigs', checked: true },
                { name: 'videos', label: 'Music Videos', checked: true },
                { name: 'noteworthy', label: 'Noteworthy Events', checked: true },
                { name: 'photo', label: 'Photo Sessions', checked: true },
                { name: 'recordings', label: 'Recording Sessions', checked: true },
                { name: 'releases', label: 'Releases', checked: true },
            ],
            filterYear: null,
            visibleEvents: TimelineData.getNumberOfEvents()
        };

        this.resetFilters = this.resetFilters.bind(this);
        this.updateState = this.updateState.bind(this);
        this.updateVisibleEventCount = this.updateVisibleEventCount.bind(this);
    }

    resetFilters() {
        this.updateState({
            filters: [...this.state.filters].map((filter, i) => { filter.checked = true; return filter; }),
            filterYear: null,
            visibleEvents: TimelineData.getNumberOfEvents()
        });
    }

    updateState(newState:any) {
        this.setState(newState);
    }

    updateVisibleEventCount(state:any) {
        if(state.visibleEvents) {
            let visibleEventCount = state.visibleEvents;
            if(visibleEventCount != this.state.visibleEvents) {
                this.setState({visibleEvents:visibleEventCount});
            }
        }
    }

    render() {
      return(
        <div className="main" id="top">
            <div className="stickyPanel">
                <MediaQuery query="(min-device-width: 768px)">
                    <div className="menuSpacer"></div>
                    <TimelineNavigation />
                    <div className="menuSpacer"></div>
                    <TimelineFilters filters={this.state.filters} filtersActive={this.state.filtersActive} onChange={this.updateState} onReset={this.resetFilters}/>
                    <div className="menuSpacer"></div>
                </MediaQuery>
                <MediaQuery query="(max-device-width: 767px)">
                    <TransitionGroup>
                        <CSSTransition classNames="" key="navigation-transition-group" timeout={500}>
                            <div>
                                <TimelineNavigation />
                            </div>
                        </CSSTransition>
                    </TransitionGroup>
                    <TransitionGroup>
                        <CSSTransition classNames="" key="filter-transition-group" timeout={500}>
                            <div>
                                <TimelineFilters filters={this.state.filters} filtersActive={this.state.filtersActive} onChange={this.updateState} onReset={this.resetFilters} />
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
                <span className="filterText">Showing { this.state.visibleEvents } of { TimelineData.getNumberOfEvents() } total events.</span>
                <br />
                <div className="disputed">
                    Events shown in <span>RED</span> are missing sources and/or the accuracy is in dispute.
                </div>
                <br />
                <Timeline style={{ width: '60%' }}>
                    <TimelineEvents filters={this.state.filters} filterYear={this.state.filterYear} onChange={this.updateVisibleEventCount}/>
                </Timeline>
                <br />
                <br />
                <Footer />
            </div>
        </div>);
    }
}