import * as React from "react";
import * as ReactDOM from "react-dom";
import TimelineData from './data/timelineData.js';

export default class TodayInHistory extends React.Component<any, any> {

    generateHistoryList() {
        const today = this.getToday();
        const events = new Array();
        const history = events.concat(TimelineData.generateHistory(today));

        if(history[0] != undefined && history.length > 0) {
            return <div><ul className="historyList">{this.generateHtml(history)}</ul></div>;
        } else {
            return <div>No events for {today}.</div>;
        }
    }

    getToday() {
        const date = new Date();
        return date.toLocaleString('en-us', { month: 'long' }) + " " + date.getDate();
    }

    generateHtml(events: any) {
        return [...events]
            .map((event, i) => <li key={"event_today_" + i}><i className="material-icons">{TimelineData.getIcon(event.type)}</i><span className="historyItem"><b>Today in {event.year}:</b> {event.description}</span></li>);
    }

    render() {
      return(
<div>
    <h3>Today In Oasis History ({this.getToday()})</h3>
    <div className="mainText">
        {this.generateHistoryList()}
    </div>
</div>);
    }
}