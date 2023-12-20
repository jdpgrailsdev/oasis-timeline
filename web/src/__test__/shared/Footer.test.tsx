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
import { Context as ResponsiveContext } from 'react-responsive'
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Footer from "../../shared/Footer";
import {HashRouter} from "react-router-dom";

describe('footer tests', () => {

    const OLD_ENV = process.env;

    beforeEach(() => {
        jest.resetModules();
        process.env = { ...OLD_ENV };
    });

    afterEach(() => {
        process.env = OLD_ENV;
    });

    test('test rendering the footer component for non-mobile', () => {
        const updatedAt = "Wednesday, March 17, 2021";
        process.env.REACT_APP_UPDATED_AT = updatedAt;

        render(
            <ResponsiveContext.Provider value={{ deviceWidth: 1000 }}>
                <HashRouter>
                    <Footer />
                </HashRouter>
            </ResponsiveContext.Provider>
        );

        const content = screen.getAllByTestId("footer-test");
        const footer = content.pop();
        expect(footer).toBeDefined();
        const copyright = screen.getByTestId("footer-copyright-test");
        expect(copyright).toHaveTextContent('© 2024 / About / Terms / GitHub / Twitter');
        const lastUpdated = screen.getByTestId("footer-last-updated-test");
        expect(lastUpdated).toHaveTextContent('Last Updated: ' + process.env.REACT_APP_UPDATED_AT);
    })

    test('test rendering the footer component for mobile', () => {
        const updatedAt = "Wednesday, March 17, 2021";
        process.env.REACT_APP_UPDATED_AT = updatedAt;

        render(
            <ResponsiveContext.Provider value={{ deviceWidth: 450 }}>
                <HashRouter>
                    <Footer />
                </HashRouter>
            </ResponsiveContext.Provider>
        );

        const content = screen.getAllByTestId("footer-test");
        const footer = content.pop();
        expect(footer).toBeDefined();
        const copyright = screen.getByTestId("footer-copyright-test");
        expect(copyright).toHaveTextContent('© 2024 /');
        const lastUpdated = screen.getByTestId("footer-last-updated-test");
        expect(lastUpdated).toHaveTextContent('Last Updated');
        const lastUpdatedTimestamp = screen.getByTestId("footer-last-updated-timestamp-test");
        expect(lastUpdatedTimestamp).toHaveTextContent(process.env.REACT_APP_UPDATED_AT);
    });
});