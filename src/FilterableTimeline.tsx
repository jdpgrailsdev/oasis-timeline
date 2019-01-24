import * as React from "react";
import * as ReactDOM from "react-dom";
import { Timeline, TimelineEvent } from "react-event-timeline";
import { CSSTransition, Transition, TransitionGroup } from "react-transition-group";
import TimelineEvents from "./TimelineEvents";

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
            ]
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.toggleFilters = this.toggleFilters.bind(this);
    }

    handleInputChange(event: any) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;

        const filters = this.state.filters;
        const filterIndex = filters.findIndex(function(filter: any) { return filter.name === target.name; });
        filters[filterIndex].checked = value;
        this.setState({ filters: filters});

        this.filterTimeline(target.name, value);
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
            return filters;
        } else {
            return <div></div>;
        }
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