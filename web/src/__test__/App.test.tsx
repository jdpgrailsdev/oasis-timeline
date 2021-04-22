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
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
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
        const updatedAt = "Wednesday, March 17, 2021";
        process.env.REACT_APP_UPDATED_AT = updatedAt;

        render(
            <ResponsiveContext.Provider value={{ deviceWidth: 1000 }}>
                <App />
            </ResponsiveContext.Provider>
        );

        const app = screen.getByTestId("app-top-test");
        expect(app).toBeDefined();
        const mainContent = screen.getByTestId("app-main-test");
        expect(mainContent).toBeDefined();
        const links = Array.from(mainContent.querySelector('.menu').querySelectorAll('a'));
        expect(links.find(e => e.href == window.location)).toHaveTextContent('Home');
        expect(links.find(e => e.href == window.location + 'about')).toHaveTextContent('About');
        expect(links.find(e => e.href == window.location + 'contributing#disqus_thread')).toHaveTextContent('Contributing');
        expect(links.find(e => e.href == window.location + 'sources')).toHaveTextContent('Sources');
        expect(links.find(e => e.href == window.location + 'timeline')).toHaveTextContent('Timeline');
    });

    test('test rendering the application for mobile', () => {
        const updatedAt = "Wednesday, March 17, 2021";
        process.env.REACT_APP_UPDATED_AT = updatedAt;

        render(
            <ResponsiveContext.Provider value={{ deviceWidth: 500 }}>
                <App />
            </ResponsiveContext.Provider>
        );
        const app = screen.getByTestId("app-top-test");
        expect(app).toBeDefined();
        const links = Array.from(app.querySelectorAll('a'));
        expect(links.find(e => e.href == window.location)).toHaveTextContent('Home');
        expect(links.find(e => e.href == window.location + 'about')).toHaveTextContent('About');
        expect(links.find(e => e.href == window.location + 'contributing#disqus_thread')).toHaveTextContent('Contributing');
        expect(links.find(e => e.href == window.location + 'sources')).toHaveTextContent('Sources');
        expect(links.find(e => e.href == window.location + 'timeline')).toHaveTextContent('Timeline');
    });

    test('test that when show settings is called, the default is prevented on the provided event', () => {
        let count = 0;
        const props = {};
        const app = new App(props);
        const event = {
            preventDefault: () => {
                count++
            }
        } as React.ChangeEvent<HTMLInputElement>;

        app.showSettings(event);
        expect(count).toBe(1);
    });
});