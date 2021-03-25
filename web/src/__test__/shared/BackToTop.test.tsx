import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import BackToTop from "../../shared/BackToTop";
import {HashRouter, Route} from "react-router-dom";

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
        const link = backToTop.querySelector('div').querySelector('a');
        expect(link).toHaveAttribute('href', '#' + baseUri + '#' + anchorId);
        expect(link).toHaveTextContent('Back To Top');
    });
});