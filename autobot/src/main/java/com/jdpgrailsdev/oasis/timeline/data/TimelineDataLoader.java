package com.jdpgrailsdev.oasis.timeline.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class TimelineDataLoader implements InitializingBean {

    private static final String TIMELINE_DATA_FILE_LOCATION_PATTERN = "classpath*:/js/timelineData.json";

    private final ObjectMapper objectMapper;

    private final ResourcePatternResolver resourceResolver;

    private List<TimelineData> timelineData;

    public TimelineDataLoader(final ObjectMapper objectMapper, final ResourcePatternResolver resourceResolver) {
        this.objectMapper = objectMapper;
        this.resourceResolver = resourceResolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadTimelineData();
    }

    public List<TimelineData> getTimelineData() {
        return timelineData;
    }

    public List<TimelineData> getHistory(final String today) {
        return timelineData.stream()
                .filter(t -> today.equals(t.getDate()))
                .sorted((a, b) -> a.getYear().compareTo(b.getYear()))
                .collect(Collectors.toList());
    }

    private void loadTimelineData() throws IOException {
        final Resource[] resources = resourceResolver.getResources(TIMELINE_DATA_FILE_LOCATION_PATTERN);

        if(resources.length > 0) {
            try (final Reader reader = new InputStreamReader(resources[0].getInputStream(), Charset.defaultCharset())) {
                this.timelineData = objectMapper.readValue(reader, new TypeReference<List<TimelineData>>() {});
            }
        } else {
            throw new FileNotFoundException("Unable to locate " + TIMELINE_DATA_FILE_LOCATION_PATTERN + " on the classpath.");
        }
    }
}
