import * as React from "react";
import * as ReactDOM from "react-dom";
import TimelineData from '../data/timelineData.js';
import SourceUtils from '../util/sourceUtils.js';

export default class HistoryList extends React.Component<any, any> {

    generateHtml(events: any) {
        return [...events].map((event, i) => this.generateLineItem(event, i));
    }

    generateLineItem(event: any, index: any) {
        let source = SourceUtils.generateSourceLink(event);
        let color = SourceUtils.isDisputed(event) ? 'red' : 'black';
        return <li style={{ color: color}} key={"event_today_" + index}><i className="material-icons">{TimelineData.getIcon(event.type)}</i><span className="historyItem"><b>Today in {event.year}:</b> {event.description}</span>{source}</li>;
    }

    render() {
        const today = this.props.selectedDate;
        const events = new Array();
        const history = events.concat(TimelineData.generateHistory(today));

        if(history[0] != undefined && history.length > 0) {
            return(
                <div>
                    <ul className="historyList">
                       {this.generateHtml(history)}
                    </ul>
                    <br />
                    <br />
                    <div>See the full <a href="#/timeline">timeline</a> for more events.</div>
                </div>);
        } else {
            return(
                <div>
                    <div>No events for {today}.</div>
                    <br />
                    <br />
                    <div>See the full <a href="#/timeline">timeline</a> for more events.</div>
                </div>);
        }
    }
}