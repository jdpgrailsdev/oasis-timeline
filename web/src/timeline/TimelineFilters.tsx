import * as React from "react";
import MediaQuery from "react-responsive";
import TimelineData from '../data/timelineDataLoader.js';

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
        let filters = [];
        let filterControls = [...this.props.filters]
            .map((filter, i) => <div key={"filter_div_" + filter.name + "_" + i}><i className="material-icons md-12">{TimelineData.getIcon(filter.name)}</i><label>{filter.label}</label><input name={filter.name} type="checkbox" checked={filter.checked} onChange={this.handleInputChange}/></div>);
        filters.push(<i className="material-icons md-14" key="filter_list_key">filter_list</i>);
        filters.push(<span key="filter_label" className="filterLabel">FILTERS</span>);
        filters = filters.concat(filterControls);
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
                    {this.generateFilters()}
                </MediaQuery>
                <MediaQuery query="(max-device-width: 767px)">
                    {this.generateActiveFilters()}
                </MediaQuery>
            </div>);
    }
}