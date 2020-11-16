package com.jdpgrailsdev.oasis.timeline.config;

import com.jdpgrailsdev.oasis.timeline.controller.EventPublisherController;
import com.jdpgrailsdev.oasis.timeline.controller.StatusController;
import com.jdpgrailsdev.oasis.timeline.controller.SupportController;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class ControllerConfiguration {

    @Bean
    public EventPublisherController eventPublisherController(final TwitterTimelineEventScheduler twitterTimelineEventScheduler) {
        return new EventPublisherController(twitterTimelineEventScheduler);
    }

    @Bean
    public StatusController statusController() {
        return new StatusController();
    }

    @Bean
    public SupportController supportController(final DateUtils dateUtils, final TimelineDataLoader timelineDataLoader, final TweetFormatUtils tweetFormatUtils) {
        return new SupportController(dateUtils, timelineDataLoader, tweetFormatUtils);
    }
}
