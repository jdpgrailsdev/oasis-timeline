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
import userEvent from "@testing-library/user-event";
import TimelineData from "../../data/timelineDataLoader";
import timelineData from "../../data/timelineData.json";
import additionalTimelineData from "../../data/additionalContextData.json";
import {HashRouter} from "react-router-dom";
import YearScroll from "../../shared/YearScroll";

describe('year scroll component tests', () => {

    beforeAll(() => {
        TimelineData.data = [
            {
                "description": "A description",
                "date": "Jan 1",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2016
            },
            {
                "description": "A description",
                "date": "Jan 1",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            },
            {
                "description": "A description",
                "date": "Jan 1",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2020
            }
        ];
    });

    afterAll(() => {
        TimelineData.additionalContextData = additionalTimelineData;
        TimelineData.data = timelineData;
    });

    test('test rendering the year scroll component', () => {
        const selectedYear = TimelineData.data[0].year;
        const onChangeFn = jest.fn(year => {});

        render(
            <HashRouter>
                <YearScroll selectedYear={selectedYear} onChange={onChangeFn} />
            </HashRouter>
        );

        const component = screen.getByTestId("year-scroll-test-top");
        expect(component).toBeDefined();
        const buttons = component.querySelectorAll(".yearScrollButton");
        expect(buttons.length).toBe(2);
    });

    test('test clicking on previous year button', () => {
        let result;
        const selectedYear = TimelineData.data[2].year;
        const onChangeFn = jest.fn(year => result = year);

        render(
            <HashRouter>
                <YearScroll selectedYear={selectedYear} onChange={onChangeFn} />
            </HashRouter>
        );


        userEvent.click(screen.getByText("chevron_left"));
        expect(result).toBe(TimelineData.data[2].year - 1);
    });

    test('test clicking on previous year button rolls over to most recent year', () => {
        let result;
        const selectedYear = TimelineData.data[0].year;
        const onChangeFn = jest.fn(year => result = year);

        render(
            <HashRouter>
                <YearScroll selectedYear={selectedYear} onChange={onChangeFn} />
            </HashRouter>
        );


        userEvent.click(screen.getByText("chevron_left"));
        expect(result).toBe(TimelineData.data[2].year);
    });

    test('test clicking on next year button', () => {
        let result;
        const selectedYear = TimelineData.data[0].year;
        const onChangeFn = jest.fn(year => result = year);

        render(
            <HashRouter>
                <YearScroll selectedYear={selectedYear} onChange={onChangeFn} />
            </HashRouter>
        );


        userEvent.click(screen.getByText("chevron_right"));
        expect(result).toBe(TimelineData.data[0].year + 1);
    });

    test('test clicking on next year button rolls over to the first year', () => {
        let result;
        const selectedYear = TimelineData.data[2].year;
        const onChangeFn = jest.fn(year => result = year);

        render(
            <HashRouter>
                <YearScroll selectedYear={selectedYear} onChange={onChangeFn} />
            </HashRouter>
        );


        userEvent.click(screen.getByText("chevron_right"));
        expect(result).toBe(TimelineData.data[0].year);
    });
});