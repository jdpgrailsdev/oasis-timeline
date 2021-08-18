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
import Collapsible from "../shared/Collapsible";
import researchData from './researchData';

const researchDataLoader = {

    data: researchData,

    generateResearchData(data) {
        const testId = this.generateTestId(data);

        return <div key={testId}>
            <Collapsible title={data.title}>
                <br />
                <div>Research</div>
                <ul>
                    { this.generateResearchNotes(data.notes, testId) }
                </ul>
                <div>Sources</div>
                <ul>
                    { this.generateSources(data.sources, testId) }
                </ul>
            </Collapsible>
            <br />
        </div>;
    },

    generateResearchNotes(notes, testId) {
        return [...notes].map((n, i) => this.buildNoteItem(n, i, testId));
    },

    generateSources(sources, testId) {
        if(sources.length > 0) {
            return [...sources].map((s, i) => this.buildSourceItem(s, i, testId));
        } else {
            return this.buildSourceItem({title:"No sources available"}, 0, testId);
        }
    },

    generateTestId(data) {
        return data.title.replace(/\s/g, "-").toLowerCase();
    },

    buildNoteItem(note, index, testId) {
        const dataTestId = "research-note-" + testId;
        const key = dataTestId + "-" + index;

        return <li className="researchNote" data-testid={dataTestId} key={key} dangerouslySetInnerHTML={{ __html: note}} />;
    },

    buildSourceItem(source, index, testId) {
        const dataTestId = "research-source-" + testId;
        const key = dataTestId + "-" + index;

        if(source.url) {
            return <li className="resourceSource" data-testid={dataTestId} key={key}>
                <a href={source.url} target="_blank" rel="noopener noreferrer">{source.title}</a>
            </li>;
        } else {
            return <li className="resourceSource"
                       data-testid={dataTestId} key={key}>{source.title}</li>;
        }
    },
};


export default researchDataLoader