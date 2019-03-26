import * as React from "react";
import * as ReactDOM from "react-dom";
import MediaQuery from "react-responsive";
import TimelineData from '../data/timelineData.js';

export default class TimelineFilters extends React.Component<any, any> {

    constructor(props: any) {
        super(props);

        this.handleInputChange = this.handleInputChange.bind(this);
        this.toggleFilters = this.toggleFilters.bind(this);
    }

    handleInputChange(event: any) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;

        const filters = this.props.filters;
        const filterIndex = filters.findIndex(function(filter: any) { return filter.name === target.name; });
        filters[filterIndex].checked = value;
        this.props.onChange({filters: filters});
    }

    generateActiveFilters() {
        if(this.props.filtersActive) {
            return this.generateFilters();
        } else {
            return <div></div>;
        }
    }

    generateFilters() {
        let filters = [...this.props.filters]
            .map((filter, i) => <div key={"filter_div_" + filter.name + "_" + i}><i className="material-icons md-12">{TimelineData.getIcon(filter.name)}</i><label>{filter.label}</label><input name={filter.name} type="checkbox" checked={filter.checked} onChange={this.handleInputChange}/></div>);
        filters.push(<br key="filter_space_1"/>);
        filters.push(<span key="filter_span_buttons"><button className="resetButton" type="button" onClick={this.props.onReset}>RESET</button></span>)
        return filters;
    }

    toggleFilters() {
        const filtersActiveState = !this.props.filtersActive;
        this.props.onChange({ filtersActive: filtersActiveState });
    }

    render() {
        return(
            <div className="filter">
                <MediaQuery query="(min-device-width: 768px)">
                    <i className="material-icons md-14">filter_list</i>
                    <span className="filterLabel">FILTERS</span>
                    <div className="filterGroup">
                        {this.generateFilters()}
                    </div>
                </MediaQuery>
                <MediaQuery query="(max-device-width: 767px)">
                    <button className="filterButton" type="button" onClick={this.toggleFilters}>
                        <i className="material-icons md-14">filter_list</i>
                    </button>
                    <div className="filterGroup">
                        {this.generateActiveFilters()}
                    </div>
                </MediaQuery>
            </div>);
    }
}