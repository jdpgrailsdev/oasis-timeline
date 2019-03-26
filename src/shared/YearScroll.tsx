import * as React from "react";
import * as ReactDOM from "react-dom";
import { HashLink as Link } from "react-router-hash-link";
import Select from "react-select";
import jump from 'jump.js';
import inView from 'in-view';
import TimelineData from '../data/timelineData.js';

export default class YearScroll extends React.Component<any, any> {

    constructor(props: any) {
        super(props);

        this.scrollYearLeft = this.scrollYearLeft.bind(this);
        this.scrollYearRight = this.scrollYearRight.bind(this);
    }

    getCurrentlySelectedYear() {
        return this.props.selectedYear;
    }

    scrollYearLeft() {
        let nextYear = parseInt(this.getCurrentlySelectedYear()) - 1;
        if(nextYear < TimelineData.getFirstYear()) {
            nextYear = TimelineData.getLastYear();
        }
        this.props.onChange(nextYear);
    }

    scrollYearRight(event:any) {
        let nextYear = parseInt(this.getCurrentlySelectedYear()) + 1;
        if(nextYear > TimelineData.getLastYear()) {
            nextYear = TimelineData.getFirstYear();
        }
        this.props.onChange(nextYear);
    }

    render() {
        return(
            <div className="yearScroll">
                <button className="yearScrollButton" type="button" onClick={this.scrollYearLeft}>
                    <i key="year_scroll_left" className="material-icons md-14">chevron_left</i>
                </button>
                <div>{this.getCurrentlySelectedYear()}</div>
                <button className="yearScrollButton" type="button" onClick={this.scrollYearRight}>
                    <i key="year_scroll_left" className="material-icons md-14">chevron_right</i>
                </button>
            </div>
        );
    }

}