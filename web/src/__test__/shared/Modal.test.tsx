import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import TimelineData from "../../data/timelineDataLoader";
import additionalTimelineData from "../../data/additionalContextData.json";
import timelineData from "../../data/timelineData.json";
import Modal from "../../shared/Modal";


describe("modal component tests", () => {

    beforeAll(() => {
        TimelineData.additionalContextData = {
            "July 11, 2019_noteworthy": [ "Noteworthy Data" ],
            "July 12, 2019_recordings": [ "Recordings Data" ],
            "July 13, 2020_gigs": [ "Gigs Data" ],
            "July 14, 2021_releases": [ "Releases Data" ]
        };
        TimelineData.data = [
            {
                "description": "A videos event",
                "date": "July 11",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Videos Event",
                "type": "videos",
                "year": 2018
            },
            {
                "description": "A noteworthy event",
                "date": "July 11",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Noteworthy Event",
                "type": "noteworthy",
                "year": 2019
            },
            {
                "description": "A recordings event",
                "date": "July 12",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Recordings Event",
                "type": "recordings",
                "year": 2019
            },
            {
                "description": "A gigs event",
                "date": "July 13",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Gigs Event",
                "type": "gigs",
                "year": 2020
            },
            {
                "description": "A releases event",
                "date": "July 14",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Releases Event",
                "type": "releases",
                "year": 2021
            }];
    });

    afterAll(() => {
        TimelineData.additionalContextData = additionalTimelineData;
        TimelineData.data = timelineData;
    });

    test('test rendering the modal component', () => {
        const timestamp = TimelineData.data[1].date + ", " + TimelineData.data[1].year;

        render(
            <Modal show={true} timestamp={timestamp} type={TimelineData.data[1].type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        expect(component.hasChildNodes()).toBe(true);
    });

    test('test rendering the modal component when the show property is set to false', () => {
        const timestamp = TimelineData.data[1].date + ", " + TimelineData.data[1].year;

        render(
            <Modal show={false} timestamp={timestamp} type={TimelineData.data[1].type} />
        );

        const component = screen.queryByTestId("modal-top-test")
        expect(component).toBeNull();
    });

    test('test the rendering of a noteworthy event in the modal component', () => {
        const timestamp = TimelineData.data[1].date + ", " + TimelineData.data[1].year;
        const type = TimelineData.data[1].type;
        const expected = TimelineData.additionalContextData[TimelineData.generateKey(timestamp, type)][0];

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        const item = component.querySelector("li").querySelector("i");
        expect(item).toBeDefined();
        expect(item).toHaveTextContent(expected);
    });

    test('test the rendering of a recordings event in the modal component', () => {
        const timestamp = TimelineData.data[2].date + ", " + TimelineData.data[2].year;
        const type = TimelineData.data[2].type;
        const expected = TimelineData.additionalContextData[TimelineData.generateKey(timestamp, type)][0];

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        const label = component.querySelector("span").querySelector("span");
        expect(label).toBeDefined();
        expect(label).toHaveTextContent("This session includes the recording of the following songs:");
        const item = component.querySelector("li").querySelector("i");
        expect(item).toBeDefined();
        expect(item).toHaveTextContent(expected);
    });

    test('test the rendering of a gigs event in the modal component', () => {
        const timestamp = TimelineData.data[3].date + ", " + TimelineData.data[3].year;
        const type = TimelineData.data[3].type;
        const expected = TimelineData.additionalContextData[TimelineData.generateKey(timestamp, type)][0];

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        const label = component.querySelector("span").querySelector("span");
        expect(label).toBeDefined();
        expect(label).toHaveTextContent("The set list includes the following songs:");
        const item = component.querySelector("li").querySelector("i");
        expect(item).toBeDefined();
        expect(item).toHaveTextContent(expected);
    });

    test('test the rendering of a releases event in the modal component', () => {
        const timestamp = TimelineData.data[4].date + ", " + TimelineData.data[4].year;
        const type = TimelineData.data[4].type;
        const expected = TimelineData.additionalContextData[TimelineData.generateKey(timestamp, type)][0];

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        const label = component.querySelector("span").querySelector("span");
        expect(label).toBeDefined();
        expect(label).toHaveTextContent("The track list includes:");
        const item = component.querySelector("li").querySelector("i");
        expect(item).toBeDefined();
        expect(item).toHaveTextContent(expected);
    });

    test('test the rendering of a non-supported event in the modal component', () => {
        const timestamp = TimelineData.data[0].date + ", " + TimelineData.data[0].year;
        const type = TimelineData.data[0].type;

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        expect(component).toHaveTextContent("Information currently unavailable");
    });

    test('test the rendering of event with an unmatched key in the modal component', () => {
        const timestamp = "August 1, 1993";
        const type = "recordings";

        render(
            <Modal show={true} timestamp={timestamp} type={type} />
        );

        const component = screen.getByTestId("modal-top-test");
        expect(component).toBeDefined();
        expect(component).toHaveTextContent("Information currently unavailable");
    });
});