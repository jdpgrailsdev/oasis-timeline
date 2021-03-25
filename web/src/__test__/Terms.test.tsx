import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Terms from "../Terms";
import {HashRouter} from "react-router-dom";

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
        const header = mainDiv.querySelector('h2');
        expect(header).toHaveTextContent('Terms');
    });
});