import TimelineData from '../../data/timelineDataLoader.js';
import additionalTimelineData from '../../data/additionalContextData.json'
import timelineData from '../../data/timelineData.json';

describe('timeline data loader tests', () => {

    beforeAll(() => {
        TimelineData.additionalContextData = {
            "July 12, 2019_certifications": [ "More Data" ],
        };

        TimelineData.data = [
            {
                "description": "A description",
                "date": "July 12",
                "source": { "name": "source name", "title": "source title", "url": "https://www.source/url" },
                "title": "A Test Event",
                "type": "certifications",
                "year": 2019
            },
            {
                "description": "A description 2",
                "date": "July 13",
                "source": { "name": "source name", "title": "source title", "url": "https://www.source/url" },
                "title": "A Test Event 2 ",
                "type": "gigs",
                "year": 2020
            }];
    });

    afterAll(() => {
        TimelineData.additionalContextData = additionalTimelineData;
        TimelineData.data = timelineData;
    });

    test('test generating event history for today', () => {
        const today = TimelineData.data[0].date;
        const history = TimelineData.generateHistory(today)
        expect(history.length).toBe(1);
        expect(history[0].description).toBe(TimelineData.data[0].description);
        expect(history[0].date).toBe(TimelineData.data[0].date);
        expect(history[0].source).toMatchObject(TimelineData.data[0].source);
        expect(history[0].title).toBe(TimelineData.data[0].title);
        expect(history[0].type).toBe(TimelineData.data[0].type);
    });

    test('test timeline key generation', () => {
        const timestamp =  Date.now();
        const type = 'gigs';
        expect(TimelineData.generateKey(timestamp, type)).toBe(timestamp + "_" + type);
    });

    test('test retrieving the year of the earliest and most recent events', () => {
        expect(TimelineData.getFirstYear()).toBe(TimelineData.data[0].year);
        expect(TimelineData.getLastYear()).toBe(TimelineData.data[1].year);
    });

    test('test retrieving the number of events', () => {
        expect(TimelineData.getNumberOfEvents()).toBe(TimelineData.data.length);
    })

    test('test determining the number of years covered by the events', () => {
        expect(TimelineData.getNumberOfYears()).toBe(TimelineData.data[1].year - TimelineData.data[0].year);
    });

    test('test retrieving the icons associated with the different event types', () => {
        expect(TimelineData.getIcon('certifications')).toBe('star');
        expect(TimelineData.getIcon('gigs')).toBe('music_note');
        expect(TimelineData.getIcon('noteworthy')).toBe('announcement');
        expect(TimelineData.getIcon('photo')).toBe('camera_alt');
        expect(TimelineData.getIcon('recordings')).toBe('settings_voice');
        expect(TimelineData.getIcon('releases')).toBe('album');
        expect(TimelineData.getIcon('videos')).toBe('videocam');
        expect(TimelineData.getIcon('other')).toBe('videocam');
    });

    test('test that additional context can be retrieved for an event', () => {
        expect(TimelineData.hasAdditionalContext(TimelineData.data[0].date + ", " + TimelineData.data[0].year, TimelineData.data[0].type)).toBe(true);
        expect(TimelineData.hasAdditionalContext(TimelineData.data[1].date + ", " + TimelineData.data[1].year, TimelineData.data[1].type)).toBe(false);
    });

    test('test that the check for events for a given year is successful', () => {
        expect(TimelineData.hasEventsForYear(TimelineData.data[0].year)).toBe(true);
        expect(TimelineData.hasEventsForYear(TimelineData.data[1].year)).toBe(true);
        expect(TimelineData.hasEventsForYear(2000)).toBe(false);
    })
});