/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";
import HistoryList from './HistoryList';

export default class TodayInHistory extends React.Component<any, any> {

    render() {
        const selectedDate:Date = this.props.selectedDate
        const formattedSelectedDate:String = selectedDate
            .toLocaleString('default', { timeZone: 'UTC', month: 'long' }) + " " +
            selectedDate.getUTCDate();

      return(
        <div data-testid="today-in-history-test">
            <h3 data-testid="today-in-history-h3" className="centered">This Day In Oasis History ({formattedSelectedDate})</h3>
            <br />
            <div className={"mainText centered"}>
                <HistoryList selectedDate={formattedSelectedDate} />
            </div>
        </div>);
    }
}