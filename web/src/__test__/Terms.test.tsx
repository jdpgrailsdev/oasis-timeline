import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Terms from "../Terms";
import {createHashHistory} from "history";
import {HashRouter, Route} from "react-router-dom";

describe('terms and conditions page tests', () => {

    test('test rendering the about page', () => {
        const history = createHashHistory();
        history.push("/terms")
        render(
            <HashRouter>
                <Route path="/terms" component={Terms}/>
            </HashRouter>
        );
        const content = screen.getAllByTestId("terms-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const header = mainDiv.querySelector('h2');
        expect(header).toHaveTextContent('Terms');
    });

});