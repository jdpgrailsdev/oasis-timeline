import * as React from "react";
import Timeline from "react-event-timeline";
import { CSSTransition, TransitionGroup } from "react-transition-group";
import MediaQuery from "react-responsive";
import BackToTop from "../shared/BackToTop";
import TimelineEvents from "./TimelineEvents";
import TimelineData from '../data/timelineDataLoader.js';
import TimelineFilters from "./TimelineFilters";
import TimelineFilterGroup from "./TimelineFilterGroup";
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
            navigationActive: false,
            toggleVisibility: false,
            visibleEvents: TimelineData.getNumberOfEvents()
        };

        this.resetFilters = this.resetFilters.bind(this);
        this.updateState = this.updateState.bind(this);
        this.updateVisibleEventCount = this.updateVisibleEventCount.bind(this);
    }

    resetFilters() {
        this.updateState({
            filters: [...this.state.filters].map((filter, i) => { filter.checked = true; return filter; }),
            visibleEvents: TimelineData.getNumberOfEvents()
        });
    }

    updateState(newState:any) {
        newState.toggleVisibility = !this.state.toggleVisibility;
        this.setState(newState);
    }

    updateVisibleEventCount(state:any) {
        if(state.visibleEvents) {
            let visibleEventCount = state.visibleEvents;
            if(visibleEventCount !== this.state.visibleEvents) {
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
                    <TimelineNavigation navigationActive="true" />
                    <div className="menuSpacer"></div>
                    <TimelineFilters filters={this.state.filters} filtersActive="true" onChange={this.updateState} onReset={this.resetFilters}/>
                    <div className="menuSpacer"></div>
                </MediaQuery>
                <MediaQuery query="(max-device-width: 767px)">
                    <TransitionGroup>
                        <CSSTransition classNames="" key="timeline-transition-group" timeout={500}>
                            <div>
                                <TimelineFilterGroup filters={this.state.filters} filtersActive={this.state.filtersActive} navigationActive={this.state.navigationActive} onChange={this.updateState} onReset={this.resetFilters} isVisible={this.state.toggleVisibility} />
                            </div>
                        </CSSTransition>
                    </TransitionGroup>
                </MediaQuery>
            </div>
            <br />
            <div className="timelinePanel">
                <h2>Timeline</h2>
                <span className="filterText">Showing { this.state.visibleEvents } of { TimelineData.getNumberOfEvents() } total events.</span>
                <br />
                <div className="disputed">
                    Events shown in <span>RED</span> are missing sources and/or the accuracy is in dispute.
                </div>
                <div className="legend">
                    Events shown with an <i className="material-icons md-12">library_books</i> icon contain a link to source material (opens in new window/tab).
                </div>
                <div className="legend">
                    Events shown with an <i className="material-icons md-12">info</i> icon include an expandable section with additional context (click to show/hide).
                </div>
                <br />
                <Timeline style={{ width: '65%' }}>
                    <TimelineEvents filters={this.state.filters} onChange={this.updateVisibleEventCount}/>
                </Timeline>
                <br id="bottom" />
                <br />
                <BackToTop baseUri="/timeline" anchorId="top" />
                <br />
                <br />
                <br />
                <br />
            </div>
        </div>);
    }
}