import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Contributing from "../Contributing";
import {HashRouter} from "react-router-dom";
import TimelineData from "../data/timelineDataLoader";
import timelineData from "../data/timelineData.json";

describe('contributing page tests', () => {

    beforeAll(() => {
        TimelineData.data = [
            {
                "description": "A description",
                "date": "July 12",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            },
            {
                "description": "A description 2",
                "date": "July 13",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event 2 ",
                "type": "gigs",
                "year": 2020
            },
            {
                "description": "A disputed description",
                "date": "July 14",
                "disputed": true,
                "source": {"name": "disputed", "title": "disputed", "url": "https://www.source/url/disputed"},
                "title": "A Disputed Test Event",
                "type": "videos",
                "year": 2021
            }];
    });

    afterAll(() => {
        TimelineData.data = timelineData;
    });

    test('test rendering the contributing page', () => {
        render(
            <HashRouter>
                <Contributing />
            </HashRouter>
        );
        const content = screen.getAllByTestId("contributing-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const disputed = screen.getByTestId("disputed-source-test");
        expect(disputed).toHaveTextContent(TimelineData.data[2].title);
    });
});