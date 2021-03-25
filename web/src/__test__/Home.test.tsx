import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Home from "../Home";
import {HashRouter} from "react-router-dom";
import TimelineData from "../data/timelineDataLoader";
import timelineData from "../data/timelineData.json";
import TodayInHistory from "../shared/TodayInHistory";

describe('home page tests', () => {

    let todayInHistory:TodayInHistory;

    beforeAll(() => {
        todayInHistory = new TodayInHistory(null, null);
    });

    afterAll(() => {
        TimelineData.data = timelineData;
    });

    test('test rendering the home page with matching events', () => {
        TimelineData.data = [
            {
                "description": "A description",
                "date": todayInHistory.getToday(),
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            }];

        render(
            <HashRouter>
                <Home />
            </HashRouter>
        );

        const content = screen.getAllByTestId("home-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();

        const todayInHistoryContent = screen.getAllByTestId("today-in-history-test");
        const history = todayInHistoryContent.pop();
        expect(history).toBeDefined()
        const header = history.querySelector('h3');
        expect(header).toHaveTextContent('Today In Oasis History (' + todayInHistory.getToday() + ')');
        const item = history.querySelector('.historyList').querySelector('.historyItem');
        expect(item).toHaveTextContent('Today in ' + TimelineData.data[0].year + ': ' + TimelineData.data[0].description);
        const source = history.querySelector('.sourceLink').querySelector('a')
        expect(source).toHaveAttribute('href', TimelineData.data[0].source.url);
        expect(source).toHaveAttribute('title', TimelineData.data[0].source.name + ' - ' + TimelineData.data[0].source.title);
    });

    test('test rendering the home page without matching events', () => {
        TimelineData.data = [
            {
                "description": "A description",
                "date": todayInHistory.getToday() + "_foo",
                "source": {"name": "source name", "title": "source title", "url": "https://www.source/url"},
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            }];

        render(
            <HashRouter>
                <Home />
            </HashRouter>
        );

        const content = screen.getAllByTestId("home-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();

        const todayInHistoryContent = screen.getAllByTestId("today-in-history-test");
        const history = todayInHistoryContent.pop();
        expect(history).toBeDefined()
        const header = history.querySelector('h3');
        expect(header).toHaveTextContent('Today In Oasis History (' + todayInHistory.getToday() + ')');
        const source = history.querySelector('.mainText').querySelector('div').querySelector('div');
        expect(source).toHaveTextContent('There are no events for ' + todayInHistory.getToday());
    });

    test('test rendering the home page without any events', () => {
        TimelineData.data = [];

        render(
            <HashRouter>
                <Home />
            </HashRouter>
        );

        const content = screen.getAllByTestId("home-top-test");
        const mainDiv = content.pop();
        expect(mainDiv).toBeDefined();

        const todayInHistoryContent = screen.getAllByTestId("today-in-history-test");
        const history = todayInHistoryContent.pop();
        expect(history).toBeDefined()
        const header = history.querySelector('h3');
        expect(header).toHaveTextContent('Today In Oasis History (' + todayInHistory.getToday() + ')');
        const source = history.querySelector('.mainText').querySelector('div').querySelector('div');
        expect(source).toHaveTextContent('There are no events for ' + todayInHistory.getToday());
    });
});