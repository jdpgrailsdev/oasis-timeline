import * as React from "react";
import * as ReactDOM from "react-dom";
import { HashLink as Link } from "react-router-hash-link";
import MediaQuery from "react-responsive";
import Select from "react-select";
import jump from 'jump.js';
import TimelineData from './data/timelineData.js';

export default class TimelineNavigation extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            navigationActive: false,
        };
    }

    generateActiveNavigation() {
        if(this.state.navigationActive) {
            return this.generateNavigation();
        } else {
            return <div></div>;
        }
    }

    generateNavigation() {
        let menu = [];
        menu.push(<div key="navigation_year_jump_label" className="yearJump"><i className="material-icons md-12">today</i><label>Year</label></div>);
        menu.push(<div key="navigation_year_jump_select"><Select name="yearJump" isClearable={true} isDisabled={false} openMenuOnFocus={true} options={this.generateYearsForNavigation()} className="yearJumpSelect" onChange={this.jumpToYear}/></div>);
        menu.push(<div key="navigation_back_label" className="navigationBack"><i className="material-icons md-14">arrow_back</i><Link to="/timeline#top">Back To Top</Link></div>);
        return menu;
    }

    generateYearsForNavigation() {
        const years = [];

        for(var year=TimelineData.getFirstYear(); year<=TimelineData.getLastYear(); year++) {
            years.push({value: year, label: year});
        }

        return years;
    }

    jumpToYear(event: any) {
        const year = event != null ? event.value : null;
        if(year != null) {
            jump("[id='" + year + "']");
        }
    }

    toggleNavigation() {
        const navigationActiveState = !this.state.navigationActive;
        this.setState({ navigationActive: navigationActiveState });
    }

    render() {
        return(
        <div className="navigation">
            <MediaQuery query="(min-device-width: 768px)">
                <i key="navigation_icon" className="material-icons md-14">navigation</i>
                <span key="navigation_label" className="navigationLabel">NAVIGATION</span>
                { this.generateNavigation() }
            </MediaQuery>
            <MediaQuery query="(max-device-width: 767px)">
                <button className="navigationButton" type="button" onClick={() => { this.toggleNavigation() }}>
                    <i className="material-icons md-14">navigation</i>
                </button>
                <div className="navigationGroup">
                    { this.generateActiveNavigation() }
                </div>
            </MediaQuery>
        </div>);
    }
}