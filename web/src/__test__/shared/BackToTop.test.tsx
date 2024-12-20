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
import BackToTop from "../../shared/BackToTop";
import {HashRouter} from "react-router";

describe('back to top tests', () => {

    test('test rendering the back to top component', () => {
        const baseUri = "/home";
        const anchorId = "top";
        render(
            <HashRouter>
                <BackToTop baseUri={baseUri} anchorId={anchorId} />
            </HashRouter>
        );

        const content = screen.getAllByTestId("back-to-top-test");
        const backToTop = content.pop();
        expect(backToTop).toBeDefined();
        if (typeof backToTop !== 'undefined') {
            const div = backToTop.querySelector('div');
            if (div !== null) {
                const link = div.querySelector('a');
                expect(link).toHaveAttribute('href', '#' + baseUri + '#' + anchorId);
                expect(link).toHaveTextContent('Back To Top');
            } else {
                fail('Back To Top DIV object is null.');
            }
        } else {
            fail('Back To Top object is undefined.');
        }
    });
});