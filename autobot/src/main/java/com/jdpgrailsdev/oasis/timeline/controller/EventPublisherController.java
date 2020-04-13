package com.jdpgrailsdev.oasis.timeline.controller;

import com.jdpgrailsdev.oasis.timeline.schedule.TwitterTimelineEventScheduler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/publish")
public class EventPublisherController {

    private final TwitterTimelineEventScheduler twitterTimelineEventScheduler;

    public EventPublisherController(final TwitterTimelineEventScheduler twitterTimelineEventScheduler) {
        this.twitterTimelineEventScheduler = twitterTimelineEventScheduler;
    }

    @RequestMapping("events")
    @ResponseBody
    public void publishEvents() {
        twitterTimelineEventScheduler.publishTimelineTweet();
    }
}
