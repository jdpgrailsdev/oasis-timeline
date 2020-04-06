package com.jdpgrailsdev.oasis.timeline.data;

import com.jdpgrailsdev.oasis.timeline.util.Generated;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class TimelineData {

    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("MMMM d, yyyy").toFormatter();

    private String description;

    private String date;

    private TimelineDataSource source;

    private String title;

    private TimelineDataType type;

    private Integer year;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public TimelineDataSource getSource() {
        return source;
    }

    public void setSource(final TimelineDataSource source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public TimelineDataType getType() {
        return type;
    }

    public void setType(final TimelineDataType type) {
        this.type = type;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(final Integer year) {
        this.year = year;
    }

    @Override
    @Generated
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        return result;
    }

    @Override
    @Generated
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TimelineData other = (TimelineData) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (type != other.type)
            return false;
        if (year == null) {
            if (other.year != null)
                return false;
        } else if (!year.equals(other.year))
            return false;
        return true;
    }

    @Override
    @Generated
    public String toString() {
        return "TimelineData [description=" + description + ", date=" + date + ", source=" + source + ", title=" + title
                + ", type=" + type + ", year=" + year + "]";
    }

    public Instant toInstant() {
        return LocalDate.parse(String.format("%s, %s", getDate(), getYear()), DATE_FORMATTER).atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
