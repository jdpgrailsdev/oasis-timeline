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
import React from "react";
import { render, screen } from "@testing-library/react";
import '@testing-library/jest-dom';
import TodayInHistory from "../../shared/TodayInHistory";
import {HashRouter} from "react-router";
import TimelineData from "../../data/timelineDataLoader";
import additionalTimelineData from "../../data/additionalContextData.json";
import timelineData from "../../data/timelineData.json";

describe('today in history tests', () => {

    let today:Date;
    let todayString:String;
    let todayInHistory:TodayInHistory;

    beforeAll(() => {
        todayInHistory = new TodayInHistory(null);
        today = new Date()
        todayString = today.toLocaleString('default', { month: 'long' }) + " " +
            today.getDate();

        TimelineData.data = [
            {
                "description": "A description",
                "date": todayString,
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            }];
    });

    afterAll(() => {
        TimelineData.additionalContextData = additionalTimelineData;
        TimelineData.data = timelineData;
    });

    test('test rendering the today in history component', () => {
        render(
            <HashRouter>
                <TodayInHistory selectedDate={today}/>
            </HashRouter>
        );

        const content = screen.getAllByTestId("today-in-history-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        if (typeof mainDiv !== 'undefined') {
            const header = mainDiv.querySelector('h3');
            expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
            const historyList = mainDiv.querySelector('.historyList');
            if (historyList !== null) {
                const item = historyList.querySelector('.historyItem');
                expect(item).toHaveTextContent('In ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
            } else {
                fail('History list object is null.');
            }
            const sourceLink = mainDiv.querySelector('.sourceLink');
            if (sourceLink !== null) {
                const source = sourceLink.querySelector('a')
                expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
                expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);
            } else {
                fail('Source link object is null.');
            }
        } else {
            fail('Main DIV object is undefined.');
        }
    });

    test('test rendering the today in history component with no events', () => {
        TimelineData.data = [];

        render(
            <HashRouter>
                <TodayInHistory selectedDate={today} />
            </HashRouter>
        );

        const content = screen.getAllByTestId("today-in-history-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        if (typeof mainDiv !== 'undefined') {
            const header = mainDiv.querySelector('h3');
            expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
            const mainText = mainDiv.querySelector('.mainText');
            if (mainText !== null) {
                const mainTextDiv = mainText.querySelector('div');
                if (mainTextDiv !== null) {
                    const source = mainTextDiv.querySelector('div');
                    expect(source).toHaveTextContent('There are no events for ' + todayString);
                } else {
                    fail('Main text DIV object is null.');
                }
            } else {
                fail('Main text object is null.');
            }
        } else {
            fail('Main DIV object is undefined.');
        }
    });

});