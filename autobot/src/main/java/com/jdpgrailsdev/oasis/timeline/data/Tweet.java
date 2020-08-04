package com.jdpgrailsdev.oasis.timeline.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

public class Tweet {

    private static final GeoLocation LOCATION = new GeoLocation(53.422201, -2.208914);

    public static final Integer TWEET_LIMIT = 280;

    /**
     * Accounts for the emoji and elipses added to multi-part weets.
     */
    private static final Integer ADDITIONAL_CHARACTERS = 4;

    private static final Double REDUCTION_PERCENTAGE = 0.85;

    private final List<String> messages;

    public Tweet(final String text) throws TwitterException {
        if(StringUtils.hasText(text)) {
            if(text.length() > TWEET_LIMIT) {
                messages = splitTweet(text);
            } else {
                messages = Lists.newArrayList(text);
            }
        } else {
            throw new TwitterException("Tweet message may not be blank.");
        }
    }

    public List<String> getMessages() {
        return ImmutableList.copyOf(messages);
    }

    @JsonIgnore
    public StatusUpdate getMainTweet() {
        return createStatusUpdate(messages.stream().findFirst().get(), null);
    }

    @JsonIgnore
    public List<StatusUpdate> getReplies(final Long inReplyToStatusId) {
        return messages.stream().skip(1).map(r -> createStatusUpdate(r, inReplyToStatusId)).collect(Collectors.toList());
    }

    private StatusUpdate createStatusUpdate(final String text, final Long inReplyToStatusId) {
        final StatusUpdate update = new StatusUpdate(text.trim());
        update.setDisplayCoordinates(true);
        update.setLocation(LOCATION);
        if(inReplyToStatusId != null) {
            update.setInReplyToStatusId(inReplyToStatusId);
        }
        return update;
    }

    private List<String> splitTweet(final String text) {
        final List<String> tweets = Lists.newArrayList();
        int size = text.length();

        while(size >= (TWEET_LIMIT - ADDITIONAL_CHARACTERS)) {
            size = Double.valueOf(Math.floor(size*REDUCTION_PERCENTAGE)).intValue();
        }

        final StringBuilder builder = new StringBuilder();
        final List<String> words = Splitter.on(' ').splitToList(text);
        for(final String word : words) {
            if((builder.length() + word.length()) <= size) {
                builder.append(" ");
                builder.append(word);
            } else {
                final boolean useElipses = !builder.toString().trim().endsWith(".");
                if(useElipses) {
                    builder.append("...");
                }
                tweets.add(builder.toString().trim());
                builder.setLength(0);
                if(useElipses) {
                    builder.append("... ");
                }
                builder.append(word);
            }
        }
        tweets.add(builder.toString().trim());
        return tweets;
    }
}
