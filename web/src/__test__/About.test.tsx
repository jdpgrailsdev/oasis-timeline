import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import About from "../About";
import {HashRouter, Route} from "react-router-dom";
import { createHashHistory } from "history";

describe('about page tests', () => {

    test('test rendering the about page', () => {
        const history = createHashHistory();
        history.push("/about")
        render(
            <HashRouter>
                <Route path="/about" component={About}/>
            </HashRouter>
        );
        const content = screen.getAllByTestId("about-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const header = mainDiv.querySelector('h2');
        expect(header).toHaveTextContent('About');
    });

});