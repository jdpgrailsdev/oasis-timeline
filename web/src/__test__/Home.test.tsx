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
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Home from "../Home";
import {HashRouter} from "react-router-dom";
import TimelineData from "../data/timelineDataLoader";
import timelineData from "../data/timelineData.json";
import TodayInHistory from "../shared/TodayInHistory";
import {Context as ResponsiveContext} from "react-responsive";
import { configure, shallow } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';

describe('home page tests', () => {

    let today:Date;
    let todayString:String;
    let todayInHistory:TodayInHistory;

    beforeAll(() => {
        configure({ adapter: new Adapter() })

        todayInHistory = new TodayInHistory(null, null);
        today = new Date()
        todayString = today.toLocaleString('default', { month: 'long' }) + " " +
            today.getDate();
    });

    afterAll(() => {
        TimelineData.data = timelineData;
    });

    test('test that the state is only updated when a different selected date is provided', () => {
        let today = new Date();
        let wrapper = shallow(<Home />);

        // Test the initial state is set to today
        expect(wrapper.state("selectedDate").getUTCFullYear()).toStrictEqual(today.getUTCFullYear());
        expect(wrapper.state("selectedDate").getUTCMonth()).toStrictEqual(today.getUTCMonth());
        expect(wrapper.state("selectedDate").getUTCDate()).toStrictEqual(today.getUTCDate());

        // Test setting the state to the same date
        let newState:any = {
            selectedDate: wrapper.state("selectedDate")
        }
        wrapper.instance().selectedDate(newState);
        expect(wrapper.state("selectedDate").getUTCFullYear()).toStrictEqual(today.getUTCFullYear());
        expect(wrapper.state("selectedDate").getUTCMonth()).toStrictEqual(today.getUTCMonth());
        expect(wrapper.state("selectedDate").getUTCDate()).toStrictEqual(today.getUTCDate());

        // Test that the selected data can be changed
        let newDate = new Date(2021, 7, 1);
        newState = {
            selectedDate: newDate
        }
        wrapper.instance().selectedDate(newState);
        expect(wrapper.state("selectedDate")).toStrictEqual(newDate);

        // Test that the selected date doesn't change if not provided
        newState = {
            other: "foo"
        }
        wrapper.instance().selectedDate(newState);
        expect(wrapper.state("selectedDate")).toStrictEqual(newDate);

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
        const header = history.querySelector('h3');
        expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
        const item = history.querySelector('.historyList').querySelector('.historyItem');
        expect(item).toHaveTextContent('In ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
        const source = history.querySelector('.sourceLink').querySelector('a')
        expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
        expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);

        const heatMap = history.querySelector('event-calendar-test');
        expect(heatMap).toBeDefined();

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
        const header = history.querySelector('h3');
        expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
        const item = history.querySelector('.historyList').querySelector('.historyItem');
        expect(item).toHaveTextContent('In ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
        const source = history.querySelector('.sourceLink').querySelector('a')
        expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
        expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);

        const heatMap = history.querySelector('event-calendar-test');
        expect(heatMap).toBeDefined();
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
        const header = history.querySelector('h3');
        expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
        const source = history.querySelector('.mainText').querySelector('div').querySelector('div');
        expect(source).toHaveTextContent('There are no events for ' + todayString);

        const heatMap = history.querySelector('event-calendar-test');
        expect(heatMap).toBeDefined();
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
        const header = history.querySelector('h3');
        expect(header).toHaveTextContent('This Day In Oasis History (' + todayString + ')');
        const source = history.querySelector('.mainText').querySelector('div').querySelector('div');
        expect(source).toHaveTextContent('There are no events for ' + todayString);

        const heatMap = history.querySelector('event-calendar-test');
        expect(heatMap).toBeDefined();
    });
});