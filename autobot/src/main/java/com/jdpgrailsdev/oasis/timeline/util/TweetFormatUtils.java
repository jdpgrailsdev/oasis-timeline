package com.jdpgrailsdev.oasis.timeline.util;

import com.google.common.collect.Lists;

import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Set;

public class TweetFormatUtils {

    public static final Integer TWEET_LIMIT = 280;

    private final ITemplateEngine textTemplateEngine;

    private final Set<String> uncapitalizeExclusions;

    public TweetFormatUtils(final ITemplateEngine textTemplateEngine, final Set<String> uncapitalizeExclusions) {
        this.textTemplateEngine = textTemplateEngine;
        this.uncapitalizeExclusions = uncapitalizeExclusions;
    }

    public String generateStatusUpdateText(final Context context) {
        return textTemplateEngine.process("tweet", context);
    }

    public String prepareDescription(final String description) {
        if(StringUtils.hasText(description)) {
            return uncapitalizeDescription(trimDescription(description));
        } else {
            return description;
        }
    }

    private String trimDescription(final String description) {
        if(description.endsWith(".")) {
            return description.substring(0, description.length() - 1).trim();
        } else {
            return description.trim();
        }
    }

    private String uncapitalizeDescription(final String description) {
        if(uncapitalizeExclusions.stream().filter(exclusion -> description.startsWith(exclusion)).count() == 0) {
            return StringUtils.uncapitalize(description);
        } else {
            return description;
        }
    }

    public List<String> splitStatusText(final String text) {
        final List<String> parts = Lists.newArrayList();
        if(text.length() > TWEET_LIMIT) {
            final String[] words = text.split(" ");
            final StringBuilder builder = new StringBuilder();
            for(final String word : words) {
                if((builder.length() + word.length()) <= (TWEET_LIMIT - 3)) {
                    builder.append(" ");
                    builder.append(word);
                } else {
                    builder.append("...");
                    parts.add(builder.toString().trim());
                    builder.setLength(0);
                    builder.append("...");
                    builder.append(word);
                }
            }
            parts.add(builder.toString().trim());
        } else {
            parts.add(text);
        }
        return parts;
    }
}
