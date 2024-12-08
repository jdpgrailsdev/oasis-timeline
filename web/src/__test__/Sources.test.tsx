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
import Sources from "../Sources";
import {HashRouter} from "react-router";
import TimelineData from "../data/timelineDataLoader";
import timelineData from "../data/timelineData.json";
import SourceUtils from "../util/sourceUtils";

describe('sources page tests', () => {
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
        TimelineData.data = timelineData;
    });

    test('test rendering the sources page', () => {
        render(
            <HashRouter>
                <Sources />
            </HashRouter>
        );

        const content = screen.getAllByTestId("sources-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();

        const sourceList = screen.getAllByTestId("source-list-test");
        const sources = sourceList.pop();
        expect(sources).toBeDefined()
        if (typeof sources !== 'undefined') {
            expect(sources.childElementCount).toBe(2);
            const firstChildLink = sources.children[0].querySelector('a');
            if (firstChildLink !== null) {
                expect(firstChildLink.getAttribute('href')).toBe(TimelineData.data[2].source.url);
                expect(firstChildLink).toHaveTextContent(SourceUtils.getSourceTitle(TimelineData.data[2].source));
            } else {
                fail('First child link object is null.')
            }
            const secondChildLink = sources.children[1].querySelector('a');
            if (secondChildLink !== null) {
                expect(secondChildLink.getAttribute('href')).toBe(TimelineData.data[0].source.url);
                expect(secondChildLink).toHaveTextContent(SourceUtils.getSourceTitle(TimelineData.data[0].source));
            } else {
                fail('Second child link object is null.')
            }
        } else {
            fail('Source object is undefined.');
        }
    });
});