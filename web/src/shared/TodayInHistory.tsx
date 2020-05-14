import * as React from "react";
import HistoryList from './HistoryList';

export default class TodayInHistory extends React.Component<any, any> {

    getToday() {
        const date = new Date();
        return date.toLocaleString('en-us', { month: 'long' }) + " " + date.getDate();
    }

    render() {
      return(
        <div>
            <h3 className="centered">Today In Oasis History ({this.getToday()})</h3>
            <br />
            <div className={"mainText centered"}>
                <HistoryList selectedDate={this.getToday()} />
            </div>
        </div>);
    }
}