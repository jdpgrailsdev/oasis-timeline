import * as React from "react";
import * as ReactDOM from "react-dom";
import { Timeline, TimelineEvent } from "react-event-timeline";
import { CSSTransition, Transition, TransitionGroup } from "react-transition-group";
import Select from "react-select";
import TimelineEvents from "./TimelineEvents";
import TimelineData from './data/timelineData.js';

export default class FilterableTimeline extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            filterActive: false,
            filters: [
                { name: 'recordings', label: 'Recordings', checked: true },
                { name: 'releases', label: 'Releases', checked: true },
                { name: 'gigs', label: 'Important Gigs', checked: true },
                { name: 'noteworthy', label: 'Noteworthy Events', checked: true },
            ],
            filterYear: null
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleYearChange = this.handleYearChange.bind(this);
        this.toggleFilters = this.toggleFilters.bind(this);
    }

    handleInputChange(event: any) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;

        const filters = this.state.filters;
        const filterIndex = filters.findIndex(function(filter: any) { return filter.name === target.name; });
        filters[filterIndex].checked = value;
        this.setState({ filters: filters}, this.filterTimeline);

        //this.filterTimeline(target.name, value);
    }

    handleYearChange(event: any) {
        const year = event != null ? event.value : null;
        this.setState({ filterYear: year }, this.filterTimeline);
    }

    toggleFilters() {
        const filterState = !this.state.filterActive;
        this.setState({
            filterActive: filterState
        });
    }

    generateFilters() {
        if(this.state.filterActive) {
            let filters = []
            for(var i in this.state.filters) {
                const filter = this.state.filters[i];
                filters.push(
                    <label key={"label_" + filter.name + "_" + i}>
                        {filter.label}
                        <input name={filter.name} type="checkbox" defaultChecked={filter.checked} onChange={this.handleInputChange}/>
                    </label>
                );
            }
            filters.push(<span key="span_year"><label key="label_year">Year</label><Select isClearable={true} options={this.generateFilterYears()} className="filterYear" onChange={this.handleYearChange}/></span>);
            return filters;
        } else {
            return <div></div>;
        }
    }

    generateFilterYears() {
        const filterYears = [];

        for(var year=TimelineData.getFirstYear(); year<=TimelineData.getLastYear(); year++) {
            filterYears.push({value: year, label: year});
        }

        return filterYears;
    }

    filterTimeline() {
        const node = ReactDOM.findDOMNode(this);
        if (node instanceof HTMLElement) {
            Array.from(node.querySelectorAll('.timeline_event')).forEach(
                (element, index, array) => {
                    if(element instanceof HTMLElement) {
                        const classList = Array.from(element.classList).filter((cn:any) => cn !== 'timeline_event');
                        const eventType = classList.find((cn:any) => cn.startsWith('type_'));
                        const eventYear = classList.find((cn:any) => cn.startsWith('year_'));
                        const filter = this.state.filters.find((f:any) => eventType != null && eventType.endsWith(f.name));
                        const include = this.includeYear(eventYear) && (filter != null && filter.checked);

                        if(include) {
                            element.style.display = 'block';
                        } else {
                            element.style.display = 'none';
                        }
                    }
                }
            );
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
        <div>
            <TransitionGroup>
                <CSSTransition classNames="" key="filter" timeout={500}>
                    <div className="filter">
                        <button className="filterButton" type="button" onClick={() => { this.toggleFilters() }}>FILTERS</button>
                        <div className="filterGroup">
                            {this.generateFilters()}
                        </div>
                    </div>
                </CSSTransition>
            </TransitionGroup>
            <br />
            <div>
                <Timeline style={{ width: '65%' }}>
                    <TimelineEvents />
                </Timeline>
            </div>
        </div>);
    }
}