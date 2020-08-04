package com.jdpgrailsdev.oasis.timeline.controller;

import com.jdpgrailsdev.oasis.timeline.data.TimelineData;
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader;
import com.jdpgrailsdev.oasis.timeline.data.Tweet;
import com.jdpgrailsdev.oasis.timeline.util.DateUtils;
import com.jdpgrailsdev.oasis.timeline.util.TweetFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import twitter4j.TwitterException;

@Controller
@RequestMapping("/support")
public class SupportController {

    private static final Logger log = LoggerFactory.getLogger(SupportController.class);

    private final DateUtils dateUtils;

    private final TimelineDataLoader timelineDataLoader;

    private final TweetFormatUtils tweetFormatUtils;

    public SupportController(final DateUtils dateUtils, final TimelineDataLoader timelineDataLoader, final TweetFormatUtils tweetFormatUtils) {
        this.dateUtils = dateUtils;
        this.timelineDataLoader = timelineDataLoader;
        this.tweetFormatUtils = tweetFormatUtils;
    }

    @RequestMapping("events")
    @ResponseBody
    public List<Tweet> getEvents(@RequestParam(value = "date", required = true)final String dateString) {
        final LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        final String formattedDateString = dateUtils.formatDateTime(localDate.atStartOfDay(ZoneId.systemDefault()));
        return timelineDataLoader.getHistory(formattedDateString).stream()
                .map(this::convertEventToTweet)
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    private Tweet convertEventToTweet(final TimelineData timelineData) {
        try {
            return tweetFormatUtils.generateTweet(timelineData, timelineDataLoader.getAdditionalHistoryContext(timelineData));
        } catch (final TwitterException e) {
            log.error("Unable to generate tweet for timeline data {}.", timelineData, e);
            return null;
        }
    }
}
