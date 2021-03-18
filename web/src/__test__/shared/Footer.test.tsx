import React from "react";
import { Context as ResponsiveContext } from 'react-responsive'
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Footer from "../../shared/Footer";
import {HashRouter, Route} from "react-router-dom";
import {createHashHistory} from "history";

describe('footer tests', () => {

    const OLD_ENV = process.env;

    beforeAll(() => {
        const history = createHashHistory();
        history.push("/footer")
    });

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
            <ResponsiveContext.Provider value={{ width: 1000 }}>
                <HashRouter>
                    <Route path="/footer" component={Footer}/>
                </HashRouter>
            </ResponsiveContext.Provider>
        );

        const content = screen.getAllByTestId("footer-test");
        const footer = content.pop();
        expect(footer).toBeDefined();
        const copyright = screen.getByTestId("footer-copyright-test");
        expect(copyright).toHaveTextContent('© 2021 / About / Terms / GitHub / Twitter');
        const lastUpdated = screen.getByTestId("footer-last-updated-test");
        expect(lastUpdated).toHaveTextContent('Last Updated: ' + process.env.REACT_APP_UPDATED_AT);
    })

    test('test rendering the footer component for mobile', () => {
        const updatedAt = "Wednesday, March 17, 2021";
        process.env.REACT_APP_UPDATED_AT = updatedAt;

        render(
            <ResponsiveContext.Provider value={{ width: 450 }}>
                <HashRouter>
                    <Route path="/footer" component={Footer}/>
                </HashRouter>
            </ResponsiveContext.Provider>
        );

        const content = screen.getAllByTestId("footer-test");
        const footer = content.pop();
        expect(footer).toBeDefined();
        const copyright = screen.getByTestId("footer-copyright-test");
        expect(copyright).toHaveTextContent('© 2021 /');
        const lastUpdated = screen.getByTestId("footer-last-updated-test");
        expect(lastUpdated).toHaveTextContent('Last Updated');
        const lastUpdatedTimestamp = screen.getByTestId("footer-last-updated-timestamp-test");
        expect(lastUpdatedTimestamp).toHaveTextContent(process.env.REACT_APP_UPDATED_AT);
    });
});