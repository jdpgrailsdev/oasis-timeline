import * as React from "react";
import * as ReactDOM from "react-dom";
import HistoryList from './HistoryList';
import TimelineData from '../data/timelineDataLoader.js';
import SourceUtils from '../util/sourceUtils.js';

export default class TodayInHistory extends React.Component<any, any> {

    getToday() {
        const date = new Date();
        return date.toLocaleString('en-us', { month: 'long' }) + " " + date.getDate();
    }

    render() {
      return(
        <div>
            <h3>Today In Oasis History ({this.getToday()})</h3>
            <div className="mainText">
                <HistoryList selectedDate={this.getToday()} />
            </div>
        </div>);
    }
}