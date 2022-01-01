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
import SourceUtils from '../../util/sourceUtils.js';
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import TimelineData from "../../data/timelineDataLoader";

describe('source utils tests', () => {

    test('test generating the source link for an event', () => {
        const event = {
            "description": "A description",
            "date": "July 12",
            "source": { "name": "source name", "title": "source title", "url": "https://www.source/url" },
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };

        const sourceLink = SourceUtils.generateSourceLink(event)

        render(<div data-testid="test">{sourceLink}</div>);
        const sourceLinkContent = screen.getAllByTestId("test");
        const sourceLinkElement = sourceLinkContent.pop();
        expect(sourceLinkElement).toBeDefined();
        if (typeof sourceLinkElement !== 'undefined') {
            const sourceLink = sourceLinkElement.querySelector('.sourceLink');
            if (sourceLink !== null) {
                const generatedLink = sourceLink.querySelector('a');
                if (generatedLink !== null) {
                    expect(generatedLink.getAttribute('title')).toBe(event.source.name + " - " + event.source.title);
                    expect(generatedLink.getAttribute('href')).toBe(event.source.url);
                } else {
                    fail('Generated link object is null.')
                }
            } else {
                fail('Source link object is null.')
            }
        } else {
            fail('Source link element object is undefined.');
        }
    });

    test('test generating a source link for an event with no source information', () => {
        const blankSource = {
            "description": "A description",
            "date": "July 12",
            "source": {  },
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };

        const sourceLink2 = SourceUtils.generateSourceLink(blankSource)

        render(<div data-testid="test">{sourceLink2}</div>);
        const sourceLinkContent2 = screen.getAllByTestId("test");
        const sourceLinkElement2 = sourceLinkContent2.pop();
        expect(sourceLinkElement2).toBeDefined();
        if (typeof sourceLinkElement2 !== 'undefined') {
            const generatedLink2 = sourceLinkElement2.querySelector('.sourceLink')
            if (generatedLink2 !== null) {
                expect(generatedLink2.childElementCount).toBe(0)
            } else {
                fail('Generated link object is null.');
            }
        } else {
            fail('Source link element object is undefined.');
        }
    });

    test('test disputed events', () => {
        const disputedEvent = {
            "description": "A description",
            "date": "July 12",
            "disputed": true,
            "source": { "name": "source name", "title": "source title", "url": "https://www.source/url" },
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };
        const event = {
            "description": "A description",
            "date": "July 12",
            "source": { "name": "source name", "title": "source title", "url": "https://www.source/url" },
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };
        const badSource = {
            "description": "A description",
            "date": "July 12",
            "source": {  },
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };
        const eventWithoutSource = {
            "description": "A description",
            "date": "July 12",
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };
        expect(SourceUtils.isDisputed(disputedEvent)).toBe(true);
        expect(SourceUtils.isDisputed(event)).toBe(false);
        expect(SourceUtils.isDisputed(badSource)).toBe(true);
        expect(SourceUtils.isDisputed(eventWithoutSource)).toBe(true);
    });

    test('test generating the source title', () => {
        const source = {
           "name": "name",
           "title" : "a title",
           "url": "https://www.source/url"
        };
        const sourceWithoutTitle = {
            "name": "name",
            "url": "https://www.source/url"
        };
        const sourceWithBlankTitle = {
            "name": "name",
            "title" : "",
            "url": "https://www.source/url"
        };
        expect(SourceUtils.getSourceTitle(source)).toBe(source.name + " - " + source.title);
        expect(SourceUtils.getSourceTitle(sourceWithoutTitle)).toBe(sourceWithoutTitle.url);
        expect(SourceUtils.getSourceTitle(sourceWithBlankTitle)).toBe(sourceWithBlankTitle.url);
    });

    test('test the comparison of sources', () => {
        const sourceA = {
            "name": "A",
            "title" : "a title",
            "url": "https://www.source/url"
        };
        const sourceB = {
            "name": "B",
            "title" : "a title",
            "url": "https://www.source/url"
        };

        expect(SourceUtils.compareSources(sourceA, sourceB)).toBe(-1);
        expect(SourceUtils.compareSources(sourceB, sourceA)).toBe(1);
        expect(SourceUtils.compareSources(sourceA, sourceA)).toBe(0);
        expect(SourceUtils.compareSources(sourceB, sourceB)).toBe(0);
        expect(SourceUtils.compareSources(sourceA, null)).toBe(-1);
        expect(SourceUtils.compareSources(sourceA, undefined)).toBe(-1);
        expect(SourceUtils.compareSources(undefined, undefined)).toBe(0);
        expect(SourceUtils.compareSources(null, sourceB)).toBe(1);
        expect(SourceUtils.compareSources(undefined, sourceB)).toBe(1);
        expect(SourceUtils.compareSources(null, null)).toBe(0);
        expect(SourceUtils.compareSources(undefined, undefined)).toBe(0);
    });

    test('test generating the source list from a list of events', () => {
        const events = [
            {
                "description": "A description",
                "date": "July 12",
                "source": { "name": "source name 1", "title": "source title 1", "url": "https://www.source/url/1" },
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            },
            {
                "description": "A description 2",
                "date": "July 13",
                "source": { "name": "source name 2", "title": "source title 2", "url": "https://www.source/url/2" },
                "title": "A Test Event 2 ",
                "type": "gigs",
                "year": 2020
            }];

        render(
            <ul data-testid="test">
                {SourceUtils.generateSourceList(events)}
            </ul>
        );
        const sourceListContent = screen.getAllByTestId("test");
        const sourceList = sourceListContent.pop()
        expect(sourceList).toBeDefined();
        if (typeof sourceList !== 'undefined') {
            expect(sourceList.childElementCount).toBe(2);
            const firstChildLink = sourceList.children[0].querySelector('a');
            if (firstChildLink !== null) {
                expect(firstChildLink.getAttribute('href')).toBe(events[0].source.url);
                expect(firstChildLink).toHaveTextContent(SourceUtils.getSourceTitle(events[0].source));
            } else {
                fail('First child link object is null.')
            }
            const secondChildLink = sourceList.children[1].querySelector('a');
            if (secondChildLink !== null) {
                expect(secondChildLink.getAttribute('href')).toBe(events[1].source.url);
                expect(secondChildLink).toHaveTextContent(SourceUtils.getSourceTitle(events[1].source));
            } else {
                fail('Second child link object is null.')
            }
        } else {
            fail('Source list DIV object is undefined.');
        }
    });
});