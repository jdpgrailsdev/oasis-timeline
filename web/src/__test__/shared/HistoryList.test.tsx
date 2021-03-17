import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import TimelineData from "../../data/timelineDataLoader";
import additionalTimelineData from "../../data/additionalContextData.json";
import timelineData from "../../data/timelineData.json";
import HistoryList from "../../shared/HistoryList";

describe('history list tests', () => {

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
        TimelineData.additionalContextData = additionalTimelineData;
        TimelineData.data = timelineData;
    });

    test('test rendering the history list component with an event', () => {
        render(
            <HistoryList selectedDate={TimelineData.data[0].date}/>
        );
        const content = screen.getAllByTestId("history-list-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const item = mainDiv.querySelector('.historyList').querySelector('.historyItem');
        expect(item).toHaveTextContent('Today in ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
        const lineItem = mainDiv.querySelector('.historyList').querySelector('li');
        expect(lineItem).toHaveAttribute('style',"color: black;");
        const source = lineItem.querySelector('.sourceLink').querySelector('a');
        expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
        expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);
    });

    test('test rendering the history list component with a disputed event', () => {
        render(
            <HistoryList selectedDate={TimelineData.data[2].date}/>
        );
        const content = screen.getAllByTestId("history-list-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const item = mainDiv.querySelector('.historyList').querySelector('.historyItem');
        expect(item).toHaveTextContent('Today in ' + TimelineData.data[2].year + ': ' + TimelineData.data[2].description);
        const lineItem = mainDiv.querySelector('.historyList').querySelector('li');
        expect(lineItem).toHaveAttribute('style',"color: red;");
        const source = lineItem.querySelector('.sourceLink').querySelector('a');
        expect(source).toHaveAttribute('href', TimelineData.data[2].source.url);
        expect(source).toHaveAttribute('title', TimelineData.data[2].source.name + ' - ' + TimelineData.data[2].source.title);
    });

    test('test rendering the history list component with no events', () => {
        const selectedDate = "January 1";
        render(
            <HistoryList selectedDate={selectedDate}/>
        );
        const content = screen.getAllByTestId("history-list-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const source = mainDiv.querySelector('div');
        expect(source).toHaveTextContent('There are no events for ' + selectedDate);
    });

});