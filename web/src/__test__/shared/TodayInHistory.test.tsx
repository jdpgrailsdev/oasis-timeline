import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import TodayInHistory from "../../shared/TodayInHistory";
import {HashRouter} from "react-router-dom";
import TimelineData from "../../data/timelineDataLoader";
import additionalTimelineData from "../../data/additionalContextData.json";
import timelineData from "../../data/timelineData.json";

describe('today in history tests', () => {

    let todayInHistory:TodayInHistory;

    beforeAll(() => {
        todayInHistory = new TodayInHistory(null, null);

        TimelineData.data = [
            {
                "description": "A description",
                "date": todayInHistory.getToday(),
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            }];
    });

    afterAll(() => {
        TimelineData.additionalContextData = additionalTimelineData;
        TimelineData.data = timelineData;
    });

    test('test rendering the today in history component', () => {
        render(
            <HashRouter>
                <TodayInHistory />
            </HashRouter>
        );

        const content = screen.getAllByTestId("today-in-history-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const header = mainDiv.querySelector('h3');
        expect(header).toHaveTextContent('Today In Oasis History (' + todayInHistory.getToday() + ')');
        const item = mainDiv.querySelector('.historyList').querySelector('.historyItem');
        expect(item).toHaveTextContent('Today in ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
        const source = mainDiv.querySelector('.sourceLink').querySelector('a')
        expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
        expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);
    });

    test('test rendering the tody in history component with no events', () => {
        TimelineData.data = [];

        render(
            <HashRouter>
                <TodayInHistory />
            </HashRouter>
        );

        const content = screen.getAllByTestId("today-in-history-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined()
        const header = mainDiv.querySelector('h3');
        expect(header).toHaveTextContent('Today In Oasis History (' + todayInHistory.getToday() + ')');
        const source = mainDiv.querySelector('.mainText').querySelector('div').querySelector('div');
        expect(source).toHaveTextContent('There are no events for ' + todayInHistory.getToday());
    });

});