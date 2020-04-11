package com.jdpgrailsdev.oasis.timeline.util;

import com.jdpgrailsdev.oasis.timeline.data.TimelineDataType;

import org.thymeleaf.context.Context;

public class ContextBuilder {

    private String additionalContext;

    private String description;

    private TimelineDataType type;

    private Integer year;

    public ContextBuilder withAdditionalContext(final String additionalContext) {
        this.additionalContext = additionalContext;
        return this;
    }

    public ContextBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public ContextBuilder withType(final TimelineDataType type) {
        this.type = type;
        return this;
    }

    public ContextBuilder withYear(final Integer year) {
        this.year = year;
        return this;
    }

    public Context build() {
        final Context context = new Context();
        context.setVariable("additionalContext", additionalContext);
        context.setVariable("description", description);
        context.setVariable("type", type.name());
        context.setVariable("year", year);
        return context;
    }
}
