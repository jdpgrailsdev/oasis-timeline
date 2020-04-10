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
            Resource resource = new ClassPathResource('/js/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(_) >> { [resource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
        when:
            loader.loadTimelineData()
        then:
            loader.getTimelineData() != null
            loader.getTimelineData().size() == 7
            loader.getTimelineData().first().getDate() != null
            loader.getTimelineData().first().getDescription() != null
            loader.getTimelineData().first().getSource() != null
            loader.getTimelineData().first().getSource().getName() != null
            loader.getTimelineData().first().getSource().getTitle() != null
            loader.getTimelineData().first().getSource().getUrl() != null
            loader.getTimelineData().first().getTitle() != null
            loader.getTimelineData().first().getType() != null
            loader.getTimelineData().first().getYear() != null
    }

    def "test that when the timeline data is filtered to a given day, the correct entries are returned"() {
        setup:
            ObjectMapper mapper = new ObjectMapper()
            Resource resource = new ClassPathResource('/js/testTimelineData.json', getClass().getClassLoader())
            ResourcePatternResolver resolver = Mock(ResourcePatternResolver) {
                getResources(_) >> { [resource] as Resource[] }
            }
            TimelineDataLoader loader = new TimelineDataLoader(mapper, resolver)
            loader.afterPropertiesSet()
            String today = 'January 1'
         when:
            def result = loader.getHistory(today)
         then:
             result.size() == 4
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
}