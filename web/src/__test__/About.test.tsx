import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import About from "../About";
import {HashRouter} from "react-router-dom";

describe('about page tests', () => {

    test('test rendering the about page', () => {
        render(
            <HashRouter>
                <About />
            </HashRouter>
        );
        const content = screen.getAllByTestId("about-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const header = mainDiv.querySelector('h2');
        expect(header).toHaveTextContent('About');
    });
});