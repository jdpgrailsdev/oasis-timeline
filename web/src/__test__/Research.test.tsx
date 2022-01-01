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
import {fireEvent, render} from "@testing-library/react";
import {HashRouter} from "react-router-dom";
import React from "react";
import Research from "../Research";
import {screen} from "@testing-library/dom";
import ResearchDataLoader from "../data/researchDataLoader";
import ResearchData from "../data/researchData.json";

describe('research page tests', () => {

    beforeAll(() => {
        ResearchDataLoader.data = [
            {
                "title": "Item With Source",
                "notes": [
                    "This is a note about the event in question.",
                    "Here is another note about the event in question.",
                    "This event may have occurred on January 1, 1993."
                ],
                "sources": [
                    {
                        "title": "An Interesting Source",
                        "url": "https://some/url"
                    }
                ]
            },
            {
                "title": "Item without Source",
                "notes": [
                    "This is a note about the event without any sources.",
                    "This event may have occurred on February 1, 1993."
                ],
                "sources": [
                ]
            }
        ]
    });

    afterAll(() => {
        ResearchDataLoader.data = ResearchData;
    });

    test('test rendering the research page', () => {
        const data = ResearchDataLoader.data[0];
        const testId = ResearchDataLoader.generateTestId(data);

        render(
            <HashRouter>
                <Research />
            </HashRouter>
        );

        const content = screen.getAllByTestId("research-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();
        if (typeof mainDiv !== 'undefined') {
            const header = mainDiv.querySelector('h2');
            if (header !== null) {
                expect(header.textContent).toBe('Research');
            } else {
                fail('Header object is null.')
            }
        } else {
            fail('Main DIV object is undefined.')
        }

        const collapsibleContent = screen.getAllByTestId("collapsible-" + testId + "-test");
        const collapsible = collapsibleContent.pop();
        expect(collapsible).toBeDefined();
        const researchHeader = screen.getByTestId("collapsible-" + testId + "-header-test");
        expect(researchHeader.textContent).toBe(data.title);

        fireEvent.click(researchHeader);

        const researchNotes = screen.getAllByTestId("research-note-" + testId);
        expect(researchNotes.length).toBe(data.notes.length);
        const researchSources = screen.getAllByTestId("research-source-" + testId);
        expect(researchSources.length).toBe(data.sources.length);
    });
});