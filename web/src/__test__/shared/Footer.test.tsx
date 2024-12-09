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
import { render, screen } from "@testing-library/react";
import '@testing-library/jest-dom';
import Footer from "../../shared/Footer";
import {HashRouter} from "react-router";

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
        process.env.REACT_APP_UPDATED_AT = "Wednesday, March 17, 2021";

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
        const social = screen.getByTestId("footer-social-follow");
        expect(social).toBeDefined();
        const blueskyFollow = screen.getByTestId("bluesky-follow");
        expect(blueskyFollow).toHaveAttribute("title", "Follow on Bluesky");
        const twitterFollow = screen.getByTestId("twitter-follow");
        expect(twitterFollow).toHaveAttribute("title", "Follow on Twitter");
        const copyright = screen.getByTestId("footer-copyright-test");
        expect(copyright).toHaveTextContent('© 2024 / About / Terms / GitHub');
        const lastUpdated = screen.getByTestId("footer-last-updated-test");
        expect(lastUpdated).toHaveTextContent('Last Updated: ' + process.env.REACT_APP_UPDATED_AT);
    })

    test('test rendering the footer component for mobile', () => {
        process.env.REACT_APP_UPDATED_AT = "Wednesday, March 17, 2021";

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
        const social = screen.getByTestId("footer-social-follow");
        expect(social).toBeDefined();
        const blueskyFollow = screen.getByTestId("bluesky-follow");
        expect(blueskyFollow).toHaveAttribute("title", "Follow on Bluesky");
        const twitterFollow = screen.getByTestId("twitter-follow");
        expect(twitterFollow).toHaveAttribute("title", "Follow on Twitter");
        const copyright = screen.getByTestId("footer-copyright-test");
        expect(copyright).toHaveTextContent('© 2024 /');
        const lastUpdated = screen.getByTestId("footer-last-updated-test");
        expect(lastUpdated).toHaveTextContent('Last Updated');
        const lastUpdatedTimestamp = screen.getByTestId("footer-last-updated-timestamp-test");
        expect(lastUpdatedTimestamp).toHaveTextContent(process.env.REACT_APP_UPDATED_AT);
    });
});