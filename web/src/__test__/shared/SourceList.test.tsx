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
import TimelineData from "../../data/timelineDataLoader";
import additionalTimelineData from "../../data/additionalContextData.json";
import timelineData from "../../data/timelineData.json";
import SourceList from "../../shared/SourceList";
import SourceUtils from "../../util/sourceUtils";

describe('source list tests', () => {

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

    test('test rendering the source list component', () => {
        render(
            <SourceList/>
        );
        const content = screen.getAllByTestId("source-list-test");
        const sourceList = content.pop();
        expect(sourceList).toBeDefined()
        expect(sourceList.childElementCount).toBe(2);
        expect(sourceList.children[0].querySelector('a').getAttribute('href')).toBe(TimelineData.data[2].source.url);
        expect(sourceList.children[0].querySelector('a')).toHaveTextContent(SourceUtils.getSourceTitle(TimelineData.data[2].source));
        expect(sourceList.children[1].querySelector('a').getAttribute('href')).toBe(TimelineData.data[0].source.url);
        expect(sourceList.children[1].querySelector('a')).toHaveTextContent(SourceUtils.getSourceTitle(TimelineData.data[0].source));
    });

});