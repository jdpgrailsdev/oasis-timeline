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
import * as React from "react";
import { render, screen } from "@testing-library/react";
import '@testing-library/jest-dom';
import App from "../App";
import {Context as ResponsiveContext} from "react-responsive";

describe('app component page tests', () => {

    const OLD_ENV = process.env;

    beforeEach(() => {
        jest.resetModules();
        process.env = { ...OLD_ENV };
    });

    afterEach(() => {
        process.env = OLD_ENV;
    });

    test('test rendering the application for non-mobile', () => {
        process.env.REACT_APP_UPDATED_AT = "Wednesday, March 17, 2021";

        render(
            <ResponsiveContext.Provider value={{ deviceWidth: 1000 }}>
                <App />
            </ResponsiveContext.Provider>
        );

        const app = screen.getByTestId("app-top-test");
        expect(app).toBeDefined();
        const mainContent = screen.getByTestId("app-main-test");
        expect(mainContent).toBeDefined();
        const menu = mainContent.querySelector('.menu')
        if (menu !== null) {
            const links = Array.from(menu.querySelectorAll('a'));
            expect(links.find(e => e.href == window.location.toString() + '#/')).toHaveTextContent('Home');
            expect(links.find(e => e.href == window.location.toString() + '#/about')).toHaveTextContent('About');
            expect(links.find(e => e.href == window.location.toString() + '#/contributing#disqus_thread')).toHaveTextContent('Contributing');
            expect(links.find(e => e.href == window.location.toString() + '#/sources')).toHaveTextContent('Sources');
            expect(links.find(e => e.href == window.location.toString() + '#/timeline')).toHaveTextContent('Timeline');
        } else {
            fail('Menu DIV object is not defined.');
        }
    });

    test('test rendering the application for mobile', () => {
        process.env.REACT_APP_UPDATED_AT = "Wednesday, March 17, 2021";

        render(
            <ResponsiveContext.Provider value={{ deviceWidth: 500 }}>
                <App />
            </ResponsiveContext.Provider>
        );
        const app = screen.getByTestId("app-top-test");
        expect(app).toBeDefined();
        const links = Array.from(app.querySelectorAll('a'));
        expect(links.find(e => e.href == window.location.toString() + '#/')).toHaveTextContent('Home');
        expect(links.find(e => e.href == window.location.toString() + '#/about')).toHaveTextContent('About');
        expect(links.find(e => e.href == window.location.toString() + '#/contributing#disqus_thread')).toHaveTextContent('Contributing');
        expect(links.find(e => e.href == window.location.toString() + '#/sources')).toHaveTextContent('Sources');
        expect(links.find(e => e.href == window.location.toString() + '#/timeline')).toHaveTextContent('Timeline');
    });
});