import * as React from "react";
import * as ReactDOM from "react-dom";
import TimelineFilters from "./TimelineFilters";
import TimelineNavigation from "./TimelineNavigation";

export default class TimelineFilterGroup extends React.Component<any, any> {

    constructor(props: any) {
        super(props);

        this.toggleGroup = this.toggleGroup.bind(this);
    }

    toggleGroup() {
        const filtersActiveState = !this.props.filtersActive;
        const navigationActiveState = !this.props.navigationActive;
        this.props.onChange({ filtersActive: filtersActiveState, navigationActive: navigationActiveState });
    }

    render() {
      let visible = this.props.isVisible ? { display : 'block' } : { display : 'none' };

      return(
          <div className="filterGroup">
            <button className="filterButton" type="button" onClick={this.toggleGroup}>
                <i className="material-icons md-14">filter_list</i>
            </button>
            <div style={visible}>
                <div className="menuSpacer"></div>
                <TimelineNavigation navigationActive={this.props.navigationActive}/>
                <div className="menuSpacer"></div>
                <TimelineFilters filters={this.props.filters} filtersActive={this.props.filtersActive} onChange={this.props.onChange} onReset={this.props.onReset}/>
                <div className="menuSpacer"></div>
            </div>
        </div>
      )
    }
}