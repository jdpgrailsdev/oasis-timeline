package com.jdpgrailsdev.oasis.timeline.data

import com.fasterxml.jackson.databind.ObjectMapper

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver

import spock.lang.Specification

class TimelineDataLoaderSpec extends Specification {

    def "test that when the timeline data is loaded on bean creation, the timeline data field is populated"() {
        setup:
            ObjectMapper mapper = new ObjectMapper()
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
        when:
            loader.loadTimelineData()
        then:
            loader.timelineData != null
            loader.timelineData.size() == 7
            loader.timelineData.first().getDate() != null
            loader.timelineData.first().getDescription() != null
            loader.timelineData.first().getSource() != null
            loader.timelineData.first().getSource().getName() != null
            loader.timelineData.first().getSource().getTitle() != null
            loader.timelineData.first().getSource().getUrl() != null
            loader.timelineData.first().getTitle() != null
            loader.timelineData.first().getType() != null
            loader.timelineData.first().getYear() != null
    }

    def "test that when the timeline data is filtered to a given day, the correct entries are returned"() {
        setup:
            ObjectMapper mapper = new ObjectMapper()
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
            loader.afterPropertiesSet()
            String today = 'January 1'
         when:
            def result = loader.getHistory(today)
         then:
             result.size() == 4
    }

    def "test that when the timeline data is filtered to a given day and the timeline data has additional context, the additional context can be retrieved"() {
        setup:
            ObjectMapper mapper = new ObjectMapper()
            Resource additionalTimelineDataResource = new ClassPathResource('/json/additionalContextData.json', getClass().getClassLoader())
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [additionalTimelineDataResource] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
            loader.afterPropertiesSet()
            String today = 'January 1'
         when:
            def result = loader.getHistory(today)
         then:
             result.size() == 4
             result.each { timelineData ->
                 def additional = loader.getAdditionalHistoryContext(timelineData)
                 if(TimelineDataType.gigs == timelineData.getType()) {
                     additional != null
                     additional.size() == 3
                     additional == ['Song 1', 'Song 2', 'Song 3']
                 } else {
                     additional != null
                     additional.size() == 0
                 }
            }
    }

    def "test that when the timeline data file is unable to be located, an exception is thrown"() {
        setup:
            ObjectMapper mapper = new ObjectMapper()
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(_) >> { [] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
        when:
            loader.afterPropertiesSet()
        then:
            thrown FileNotFoundException
    }

    def "test that when the additional timeline data file is unable to be located, an exception is thrown"() {
        setup:
            ObjectMapper mapper = new ObjectMapper()
            Resource timelineDataResource = new ClassPathResource('/json/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(TimelineDataLoader.ADDITIONAL_TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [] as Resource[] }
                getResources(TimelineDataLoader.TIMELINE_DATA_FILE_LOCATION_PATTERN) >> { [timelineDataResource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
        when:
            loader.afterPropertiesSet()
        then:
            thrown FileNotFoundException
    }
}