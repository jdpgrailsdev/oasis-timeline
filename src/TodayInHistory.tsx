import * as React from "react";
import * as ReactDOM from "react-dom";
import TimelineData from './data/timelineData.js';

export default class TodayInHistory extends React.Component<any, any> {

    generateHistoryList() {
        const date = new Date();
        const today = date.toLocaleString('en-us', { month: 'long' }) + " " + date.getDate();
        const events = new Array();
        const history = events.concat(TimelineData.generateHistory(today));

        if(history[0] != undefined && history.length > 0) {
            return <div><ul className="historyList">{this.generateHtml(history)}</ul></div>;
        } else {
            return <div>No events for {today}.</div>;
        }
    }

    generateHtml(events: any) {
        let html = []

        for(const i in events) {
            const event = events[i];
            html.push(<li><i className="material-icons">{TimelineData.getIcon(event.type)}</i><b>{event.year}</b> {event.description}</li>);
        }

        return html;
    }

    render() {
      return(
<div>
    <h3>Today In Oasis History</h3>
    <div className="mainText">
        {this.generateHistoryList()}
    </div>
</div>);
    }
}