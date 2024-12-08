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
import Home from "../Home";
import {HashRouter} from "react-router";
import TimelineData from "../data/timelineDataLoader";
import timelineData from "../data/timelineData.json";
import TodayInHistory from "../shared/TodayInHistory";
import {Context as ResponsiveContext} from "react-responsive";

describe('home page tests', () => {

    let today:Date;
    let todayString:String;
    let todayInHistory:TodayInHistory;

    beforeAll(() => {
        todayInHistory = new TodayInHistory(null);
        today = new Date()
        todayString = today.toLocaleString('default', { month: 'long' }) + " " +
            today.getDate();
    });

    afterAll(() => {
        TimelineData.data = timelineData;
    });

    test('test that today\'s date is rendered', () => {
        let today = new Date();
        let todayFormatted = today.toLocaleString('default', { timeZone: 'UTC', month: 'long' }) + " " +
            today.getUTCDate();

        render(
            <HashRouter>
                <Home />
            </HashRouter>
        );

        // Test the initial state is set to today
        let todayInHistory = screen.getAllByTestId("today-in-history-h3");

        expect(todayInHistory[0].textContent).toStrictEqual("This Day In Oasis History (" + todayFormatted + ")");
    });

    test('test rendering the home page with matching events', () => {
        TimelineData.data = [
            {
                "description": "A description",
                "date": todayString,
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            }];

        render(
            <HashRouter>
                <Home />
            </HashRouter>
        );

        const content = screen.getAllByTestId("home-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();

        const todayInHistoryContent = screen.getAllByTestId("today-in-history-test");
        const history = todayInHistoryContent.pop();
        expect(history).toBeDefined()
        if (typeof history !== 'undefined') {
            const header = history.querySelector('h3');
            expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
            const historyList = history.querySelector('.historyList');
            if (historyList !== null) {
                const item = historyList.querySelector('.historyItem');
                expect(item).toHaveTextContent('In ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
            } else {
                fail('History list DIV object is null');
            }

            const sourceLink = history.querySelector('.sourceLink');
            if (sourceLink !== null) {
                const source = sourceLink.querySelector('a')
                expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
                expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);
            } else {
                fail('Source link DIV object is null.')
            }

            const heatMap = history.querySelector('event-calendar-test');
            expect(heatMap).toBeDefined();
        } else {
            fail('History DIV object is undefined.')
        }

    });

    test('test rendering the home page with matching events for mobile devices', () => {
        TimelineData.data = [
            {
                "description": "A description",
                "date": todayString,
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            }];

        render(
            <ResponsiveContext.Provider value={{ deviceWidth: 500 }}>
                <HashRouter>
                    <Home />
                </HashRouter>
            </ResponsiveContext.Provider>
        );

        const content = screen.getAllByTestId("home-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();

        const todayInHistoryContent = screen.getAllByTestId("today-in-history-test");
        const history = todayInHistoryContent.pop();
        expect(history).toBeDefined()
        if (typeof history !== 'undefined') {
            const header = history.querySelector('h3');
            expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
            const historyList = history.querySelector('.historyList');
            if (historyList !== null) {
                const item = historyList.querySelector('.historyItem');
                expect(item).toHaveTextContent('In ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
            } else {
                fail('History list DIV object is null.');
            }

            const sourceLink = history.querySelector('.sourceLink');
            if (sourceLink !== null) {
                const source = sourceLink.querySelector('a')
                expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
                expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);
            } else {
                fail('Source link DIV object is null.')
            }

            const heatMap = history.querySelector('event-calendar-test');
            expect(heatMap).toBeDefined();
        } else {
            fail('History DIV object is undefined.');
        }
    });

    test('test rendering the home page without matching events', () => {
        TimelineData.data = [
            {
                "description": "A description",
                "date": todayString + "_foo",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            }];

        render(
            <HashRouter>
                <Home />
            </HashRouter>
        );

        const content = screen.getAllByTestId("home-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();

        const todayInHistoryContent = screen.getAllByTestId("today-in-history-test");
        const history = todayInHistoryContent.pop();
        expect(history).toBeDefined()
        if (typeof history !== 'undefined') {
            const header = history.querySelector('h3');
            expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
            const mainText = history.querySelector('.mainText');
            if (mainText !== null) {
                const mainTextDiv = mainText.querySelector('div');
                if (mainTextDiv !== null) {
                    const source = mainTextDiv.querySelector('div');
                    expect(source).toHaveTextContent('There are no events for ' + todayString);
                } else {
                    fail('Main text DIV object is null.');
                }
            } else {
                fail('Main text object is null.')
            }

            const heatMap = history.querySelector('event-calendar-test');
            expect(heatMap).toBeDefined();
        } else {
            fail('History DIV object is undefined.');
        }
    });

    test('test rendering the home page without any events', () => {
        TimelineData.data = [];

        render(
            <HashRouter>
                <Home />
            </HashRouter>
        );

        const content = screen.getAllByTestId("home-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();

        const todayInHistoryContent = screen.getAllByTestId("today-in-history-test");
        const history = todayInHistoryContent.pop();
        expect(history).toBeDefined()
        if (typeof history !== 'undefined') {
            const header = history.querySelector('h3');
            expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
            const mainText = history.querySelector('.mainText');
            if (mainText !== null) {
                const mainTextDiv = mainText.querySelector('div');
                if (mainTextDiv !== null) {
                    const source = mainTextDiv.querySelector('div');
                    expect(source).toHaveTextContent('There are no events for ' + todayString);
                } else {
                    fail('Main text DIV object is null.');
                }
            } else {
                fail('Main text object is null.')
            }
            const heatMap = history.querySelector('event-calendar-test');
            expect(heatMap).toBeDefined();
        } else {
            fail('History DIV object is undefined.');
        }
    });
});