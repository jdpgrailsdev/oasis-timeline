import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Spacer from "../../shared/Spacer";

describe('spacer tests', () => {

    test('test rendering the spacer component', () => {
        render(
            <Spacer />
        );

        const content = screen.getAllByTestId("spacer-test");
        const wrapper = content.pop();
        expect(wrapper).toBeDefined();
        const spacer = wrapper.querySelector('div');
        expect(spacer).toHaveAttribute('style', 'border-bottom: 3px solid black; display: block; margin: 0px auto 10px auto; width: 65%;');
    });
});