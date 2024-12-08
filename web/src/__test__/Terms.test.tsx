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
import Terms from "../Terms";
import {HashRouter} from "react-router";

describe('terms and conditions page tests', () => {

    test('test rendering the about page', () => {
        render(
            <HashRouter>
                <Terms />
            </HashRouter>
        );
        const content = screen.getAllByTestId("terms-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        if (typeof mainDiv !== 'undefined') {
            const header = mainDiv.querySelector('h2');
            expect(header).toHaveTextContent('Terms');
        } else {
            fail('Main DIV object is undefined.');
        }
    });
});