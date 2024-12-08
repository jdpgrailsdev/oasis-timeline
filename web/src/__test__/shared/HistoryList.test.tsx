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
import TimelineData from "../../data/timelineDataLoader";
import additionalTimelineData from "../../data/additionalContextData.json";
import timelineData from "../../data/timelineData.json";
import HistoryList from "../../shared/HistoryList";

describe('history list tests', () => {

    beforeAll(() => {
        TimelineData.data = [
            {
                "description": "A description",
                "date": "July 12",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            },
            {
                "description": "A description 2",
                "date": "July 13",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event 2 ",
                "type": "gigs",
                "year": 2020
            },
            {
                "description": "A disputed description",
                "date": "July 14",
                "disputed": true,
                "source": {"name": "disputed", "title": "disputed", "url": "https://www.source/url/disputed"},
                "title": "A Disputed Test Event",
                "type": "videos",
                "year": 2021
            }];
    });

    afterAll(() => {
        TimelineData.additionalContextData = additionalTimelineData;
        TimelineData.data = timelineData;
    });

    test('test rendering the history list component with an event', () => {
        render(
            <HistoryList selectedDate={TimelineData.data[0].date}/>
        );
        const content = screen.getAllByTestId("history-list-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        if (typeof mainDiv !== 'undefined') {
            const historyList = mainDiv.querySelector('.historyList');
            if (historyList !== null) {
                const item = historyList.querySelector('.historyItem');
                expect(item).toHaveTextContent('In ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
                const lineItem = historyList.querySelector('li');
                expect(lineItem).toHaveAttribute('style', "color: black;");
                if (lineItem !== null) {
                    const sourceLink = lineItem.querySelector('.sourceLink');
                    if (sourceLink !== null) {
                        const source = sourceLink.querySelector('a');
                        expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
                        expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);
                    } else {
                        fail('Source Link DIV object is null.');
                    }
                } else {
                    fail('Line Item DIV object is null.');
                }
            } else {
                fail('History List DIV object is null.');
            }
        } else {
            fail('Main DIV object is undefined.');
        }
    });

    test('test rendering the history list component with a disputed event', () => {
        render(
            <HistoryList selectedDate={TimelineData.data[2].date}/>
        );
        const content = screen.getAllByTestId("history-list-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        if (typeof mainDiv !== 'undefined') {
            const historyList = mainDiv.querySelector('.historyList');
            if (historyList !== null) {
                const item = historyList.querySelector('.historyItem');
                expect(item).toHaveTextContent('In ' + TimelineData.data[2].year + ': ' + TimelineData.data[2].description);
                const lineItem = historyList.querySelector('li');
                expect(lineItem).toHaveAttribute('style',"color: red;");
                if (lineItem !== null) {
                    const sourceLink = lineItem.querySelector('.sourceLink');
                    if (sourceLink !== null) {
                        const source = sourceLink.querySelector('a');
                        expect(source).toHaveAttribute('href', TimelineData.data[2].source.url);
                        expect(source).toHaveAttribute('title', TimelineData.data[2].source.name + ' - ' + TimelineData.data[2].source.title);
                    } else {
                        fail('Source Link DIV object is null.');
                    }
                } else {
                    fail('Line Item DIV object is null.');
                }
            } else {
                fail('History List DIV object is null.');
            }
        } else {
            fail('Main DIV object is undefined.');
        }
    });

    test('test rendering the history list component with no events', () => {
        const selectedDate = "January 1";
        render(
            <HistoryList selectedDate={selectedDate}/>
        );
        const content = screen.getAllByTestId("history-list-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        if (typeof mainDiv !== 'undefined') {
            const source = mainDiv.querySelector('div');
            expect(source).toHaveTextContent('There are no events for ' + selectedDate);
        } else {
            fail('Main DIV object is undefined.');
        }
    });

});