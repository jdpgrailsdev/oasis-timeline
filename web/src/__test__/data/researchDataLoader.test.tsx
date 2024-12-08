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
import ResearchDataLoader from '../../data/researchDataLoader.js';
import ResearchData from '../../data/researchData.json';
import { fireEvent, render, screen } from "@testing-library/react";
import '@testing-library/jest-dom';

describe('research data loader tests', () => {

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


    test('test converting a research item to HTML', () => {
        const data = ResearchDataLoader.data[0];
        const testId = ResearchDataLoader.generateTestId(data);
        const research = ResearchDataLoader.generateResearchData(data);

        render(research);

        const content = screen.getAllByTestId("collapsible-" + testId + "-test");
        const collapsible = content.pop();
        expect(collapsible).toBeDefined();
        const header = screen.getByTestId("collapsible-" + testId + "-header-test");
        expect(header.textContent).toBe(data.title);

        fireEvent.click(header);

        const researchNotes = screen.getAllByTestId("research-note-" + testId);
        expect(researchNotes.length).toBe(data.notes.length);
        const researchSources = screen.getAllByTestId("research-source-" + testId);
        expect(researchSources.length).toBe(data.sources.length);
    });

    test('test converting a research item without any sources to HTML', () => {
        const data = ResearchDataLoader.data[1];
        const testId = ResearchDataLoader.generateTestId(data);
        const research = ResearchDataLoader.generateResearchData(data);

        render(research);

        const content = screen.getAllByTestId("collapsible-" + testId + "-test");
        const collapsible = content.pop();
        expect(collapsible).toBeDefined();
        const header = screen.getByTestId("collapsible-" + testId + "-header-test");
        expect(header.textContent).toBe(data.title);

        fireEvent.click(header);

        const researchNotes = screen.getAllByTestId("research-note-" + testId);
        expect(researchNotes.length).toBe(data.notes.length);
        const researchSources = screen.getAllByTestId("research-source-" + testId);
        expect(researchSources.length).toBe(1);
        const resourceSource = researchSources[0];
        expect(resourceSource).toHaveTextContent("No sources available");
    });
});