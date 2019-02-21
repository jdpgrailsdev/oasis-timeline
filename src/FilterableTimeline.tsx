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
import jump from 'jump.js'

export default class FilterableTimeline extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            filterActive: false,
            filters: [
                { name: 'certifications', label: 'Certified Awards', checked: true },
                { name: 'gigs', label: 'Important Gigs', checked: true },
                { name: 'videos', label: 'Music Videos', checked: true },
                { name: 'noteworthy', label: 'Noteworthy Events', checked: true },
                { name: 'photo', label: 'Photo Sessions', checked: true },
                { name: 'recordings', label: 'Recording Sessions', checked: true },
                { name: 'releases', label: 'Releases', checked: true },
            ],
            filterYear: null
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleYearChange = this.handleYearChange.bind(this);
        this.toggleFilters = this.toggleFilters.bind(this);
        this.resetFilters = this.resetFilters.bind(this);
    }

    handleInputChange(event: any) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;

        const filters = this.state.filters;
        const filterIndex = filters.findIndex(function(filter: any) { return filter.name === target.name; });
        filters[filterIndex].checked = value;
        this.setState({ filters: filters}, this.filterTimeline);
    }

    handleYearChange(event: any) {
        const year = event != null ? event.value : null;
        this.setState({ filterYear: year }, this.filterTimeline);
    }

    jumpToYear(event: any) {
        const year = event != null ? event.value : null;
        if(year != null) {
            jump("[id='" + year + "']");
        }
    }

    toggleFilters() {
        const filterState = !this.state.filterActive;
        this.setState({ filterActive: filterState }, this.filterTimeline);
    }

    resetFilters() {
        const filters = this.state.filters.map(function(filter: any) {
            filter.checked = true;
            return filter;
        });
        this.setState({
            filterActive: false,
            filters: filters,
            filterYear: null
        }, this.filterTimeline);

        this.forceUpdate();
    }

    generateActiveFilters() {
        if(this.state.filterActive) {
            return this.generateFilters();
        } else {
            return <div></div>;
        }
    }

    generateFilters() {
        let filters = [...this.state.filters]
            .map((filter, i) => <div key={"filter_div_" + filter.name + "_" + i}><i className="material-icons md-12">{TimelineData.getIcon(filter.name)}</i><label>{filter.label}</label><input name={filter.name} type="checkbox" defaultChecked={filter.checked} onChange={this.handleInputChange}/></div>);
        filters.push(<span key="filter_span_year"><i className="material-icons md-12">today</i><label key="filter_label_year">Year</label><Select isClearable={true} options={this.generateFilterYears()} className="filterYear" onChange={this.handleYearChange}/></span>);
        filters.push(<br key="filter_space_1"/>);
        filters.push(<br key="filter_space_2"/>);
        filters.push(<span key="filter_span_buttons"><button className="resetButton" type="button" onClick={() => { this.resetFilters() }}>RESET</button></span>)
        return filters;
    }

    generateFilterYears() {
        const filterYears = [];

        for(var year=TimelineData.getFirstYear(); year<=TimelineData.getLastYear(); year++) {
            filterYears.push({value: year, label: year});
        }

        return filterYears;
    }

    filterTimeline() {
        let visibleEvents = 0;
        const node = ReactDOM.findDOMNode(this);
        if (node instanceof HTMLElement) {
            Array.from(node.querySelectorAll('.timeline_event')).forEach(
                (element, index, array) => {
                    if(element instanceof HTMLElement) {
                        const classList = Array.from(element.classList).filter((cn:any) => cn !== 'timeline_event');
                        const eventType = classList.find((cn:any) => cn.startsWith('type_'));
                        const eventYear = classList.find((cn:any) => cn.startsWith('year_'));
                        const filter = this.state.filters.find((f:any) => eventType != null && eventType.endsWith(f.name));
                        const include = this.includeYear(eventYear) && this.includeByFilter(filter);

                        if(include) {
                            element.style.display = 'block';
                            visibleEvents++;
                        } else {
                            element.style.display = 'none';
                        }
                    }
                }
            );

            Array.from(node.querySelectorAll('.filterText')).forEach(
                (element, index, array) => {
                    if(element instanceof HTMLElement) {
                        element.innerHTML = "Showing " + visibleEvents + " of " +  TimelineData.getNumberOfEvents() + " total events.";
                    }
                }
            );
        }
    }

    includeByFilter(filter:any) {
        if(filter == null) {
            return true;
        } else {
            return (filter != null && filter.checked);
        }
    }

    includeYear(year:any) {
        if(this.state.filterYear != null) {
            return (year != null) ? year === ('year_' + this.state.filterYear) : true;
        } else {
            return true;
        }
    }

    render() {
      return(
        <div className="main" id="top">
            <div className="filterPanelSticky">
                <MediaQuery query="(min-device-width: 768px)">
                    <div className="filter">
                        <i className="material-icons md-14">filter_list</i>
                        <span className="filterLabel">FILTERS</span>
                        <div className="filterGroup">
                            {this.generateFilters()}
                        </div>
                    </div>
                </MediaQuery>
                <MediaQuery query="(max-device-width: 767px)">
                    <TransitionGroup>
                        <CSSTransition classNames="" key="filter" timeout={500}>
                            <div className="filter">
                                <button className="filterButton" type="button" onClick={() => { this.toggleFilters() }}>
                                    <i className="material-icons md-14">filter_list</i>
                                </button>
                                <div className="filterGroup">
                                    {this.generateActiveFilters()}
                                </div>
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
                <div className="yearJump">
                    <i className="material-icons md-18">today</i>
                    <label>Jump to Year</label>
                    <Select name="yearJump" isClearable={true} isDisabled={false} openMenuOnFocus={true} options={this.generateFilterYears()} className="yearJumpSelect" onChange={this.jumpToYear}/>
                </div>
                <br />
                <Timeline style={{ width: '65%' }}>
                    <TimelineEvents />
                </Timeline>
                <br />
                <br />
                <div className="backToTop">
                    <Link to="/timeline#top">Back To Top</Link>
                </div>
                <Footer />
            </div>
        </div>);
    }
}