import SourceUtils from '../../util/sourceUtils.js';

describe('source utils tests', () => {

    test('test disputed events', () => {
        const disputedEvent = {
            "description": "A description",
            "date": "July 12",
            "disputed": true,
            "source": { "name": "source name", "title": "source title", "url": "https://www.source/url" },
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };
        const event = {
            "description": "A description",
            "date": "July 12",
            "source": { "name": "source name", "title": "source title", "url": "https://www.source/url" },
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };
        const badSource = {
            "description": "A description",
            "date": "July 12",
            "source": {  },
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };
        const eventWithoutSource = {
            "description": "A description",
            "date": "July 12",
            "title": "A Test Event",
            "type": "certifications",
            "year": 2019
        };
        expect(SourceUtils.isDisputed(disputedEvent)).toBe(true);
        expect(SourceUtils.isDisputed(event)).toBe(false);
        expect(SourceUtils.isDisputed(badSource)).toBe(true);
        expect(SourceUtils.isDisputed(eventWithoutSource)).toBe(true);
    });

    test('test generating the source title', () => {
        const source = {
           "name": "name",
           "title" : "a title",
           "url": "https://www.source/url"
        };
        const sourceWithoutTitle = {
            "name": "name",
            "url": "https://www.source/url"
        };
        const sourceWithBlankTitle = {
            "name": "name",
            "title" : "",
            "url": "https://www.source/url"
        };
        expect(SourceUtils.getSourceTitle(source)).toBe(source.name + " - " + source.title);
        expect(SourceUtils.getSourceTitle(sourceWithoutTitle)).toBe(sourceWithoutTitle.url);
        expect(SourceUtils.getSourceTitle(sourceWithBlankTitle)).toBe(sourceWithBlankTitle.url);
    });

    test('test the comparison of sources', () => {
        const sourceA = {
            "name": "A",
            "title" : "a title",
            "url": "https://www.source/url"
        };
        const sourceB = {
            "name": "B",
            "title" : "a title",
            "url": "https://www.source/url"
        };

        expect(SourceUtils.compareSources(sourceA, sourceB)).toBe(-1);
        expect(SourceUtils.compareSources(sourceB, sourceA)).toBe(1);
        expect(SourceUtils.compareSources(sourceA, sourceA)).toBe(0);
        expect(SourceUtils.compareSources(sourceB, sourceB)).toBe(0);
        expect(SourceUtils.compareSources(sourceA, null)).toBe(-1);
        expect(SourceUtils.compareSources(sourceA, undefined)).toBe(-1);
        expect(SourceUtils.compareSources(undefined, undefined)).toBe(0);
        expect(SourceUtils.compareSources(null, sourceB)).toBe(1);
        expect(SourceUtils.compareSources(undefined, sourceB)).toBe(1);
        expect(SourceUtils.compareSources(null, null)).toBe(0);
        expect(SourceUtils.compareSources(undefined, undefined)).toBe(0);
    });
});