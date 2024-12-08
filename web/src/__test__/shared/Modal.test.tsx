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
import Modal from "../../shared/Modal";


describe("modal component tests", () => {

    beforeAll(() => {
        TimelineData.additionalContextData = {
            "July 11, 2019_noteworthy": [ "Noteworthy Data" ],
            "July 12, 2019_recordings": [ "Recordings Data" ],
            "July 13, 2020_gigs": [ "Gigs Data" ],
            "July 14, 2021_releases": [ "Releases Data" ]
        };
        TimelineData.data = [
            {
                "description": "A videos event",
                "date": "July 11",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Videos Event",
                "type": "videos",
                "year": 2018
            },
            {
                "description": "A noteworthy event",
                "date": "July 11",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Noteworthy Event",
                "type": "noteworthy",
                "year": 2019
            },
            {
                "description": "A recordings event",
                "date": "July 12",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Recordings Event",
                "type": "recordings",
                "year": 2019
            },
            {
                "description": "A gigs event",
                "date": "July 13",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Gigs Event",
                "type": "gigs",
                "year": 2020
            },
            {
                "description": "A releases event",
                "date": "July 14",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Releases Event",
                "type": "releases",
                "year": 2021
            }];
    });

    afterAll(() => {
        TimelineData.additionalContextData = additionalTimelineData;
        TimelineData.data = timelineData;
    });

    test('test rendering the modal component', () => {
        const timestamp = TimelineData.data[1].date + ", " + TimelineData.data[1].year;

        render(
            <Modal show={true} timestamp={timestamp} type={TimelineData.data[1].type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        expect(component.hasChildNodes()).toBe(true);
    });

    test('test rendering the modal component when the show property is set to false', () => {
        const timestamp = TimelineData.data[1].date + ", " + TimelineData.data[1].year;

        render(
            <Modal show={false} timestamp={timestamp} type={TimelineData.data[1].type} />
        );

        const component = screen.queryByTestId("modal-top-test")
        expect(component).toBeNull();
    });

    test('test the rendering of a noteworthy event in the modal component', () => {
        const timestamp = TimelineData.data[1].date + ", " + TimelineData.data[1].year;
        const type = TimelineData.data[1].type;
        const expected = TimelineData.additionalContextData[TimelineData.generateKey(timestamp, type)][0];

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        const lineItem = component.querySelector("li");
        if (lineItem !== null) {
            const item = lineItem.querySelector("i");
            expect(item).toBeDefined();
            expect(item).toHaveTextContent(expected);
        } else {
            fail('Line item object is null.')
        }
    });

    test('test the rendering of a recordings event in the modal component', () => {
        const timestamp = TimelineData.data[2].date + ", " + TimelineData.data[2].year;
        const type = TimelineData.data[2].type;
        const expected = TimelineData.additionalContextData[TimelineData.generateKey(timestamp, type)][0];

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        const span = component.querySelector("span");
        if (span !== null) {
            const label = span.querySelector("span");
            expect(label).toBeDefined();
            expect(label).toHaveTextContent("This session includes the recording of the following songs:");
        } else {
            fail('Span object is null.')
        }
        const lineItem = component.querySelector("li");
        if (lineItem !== null) {
            const item = lineItem.querySelector("i");
            expect(item).toBeDefined();
            expect(item).toHaveTextContent(expected);
        } else {
            fail('Line item object is null.')
        }
    });

    test('test the rendering of a gigs event in the modal component', () => {
        const timestamp = TimelineData.data[3].date + ", " + TimelineData.data[3].year;
        const type = TimelineData.data[3].type;
        const expected = TimelineData.additionalContextData[TimelineData.generateKey(timestamp, type)][0];

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        const span = component.querySelector("span");
        if (span !== null) {
            const label = span.querySelector("span");
            expect(label).toBeDefined();
            expect(label).toHaveTextContent("The set list includes the following songs:");
        } else {
            fail('Span object is null.')
        }
        const lineItem = component.querySelector("li");
        if (lineItem !== null) {
            const item = lineItem.querySelector("i");
            expect(item).toBeDefined();
            expect(item).toHaveTextContent(expected);
        } else {
            fail('Line item object is null.')
        }
    });

    test('test the rendering of a releases event in the modal component', () => {
        const timestamp = TimelineData.data[4].date + ", " + TimelineData.data[4].year;
        const type = TimelineData.data[4].type;
        const expected = TimelineData.additionalContextData[TimelineData.generateKey(timestamp, type)][0];

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        const span = component.querySelector("span");
        if (span !== null) {
            const label = span.querySelector("span");
            expect(label).toBeDefined();
            expect(label).toHaveTextContent("The track list includes:");
        } else {
            fail('Span object is null.')
        }
        const lineItem = component.querySelector("li");
        if (lineItem !== null) {
            const item = lineItem.querySelector("i");
            expect(item).toBeDefined();
            expect(item).toHaveTextContent(expected);
        } else {
            fail('Line item object is null.')
        }
    });

    test('test the rendering of a non-supported event in the modal component', () => {
        const timestamp = TimelineData.data[0].date + ", " + TimelineData.data[0].year;
        const type = TimelineData.data[0].type;

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        expect(component).toHaveTextContent("Information currently unavailable");
    });

    test('test the rendering of event with an unmatched key in the modal component', () => {
        const timestamp = "August 1, 1993";
        const type = "recordings";

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        expect(component).toHaveTextContent("Information currently unavailable");
    });
});