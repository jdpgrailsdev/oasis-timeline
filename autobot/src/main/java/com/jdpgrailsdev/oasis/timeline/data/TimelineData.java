/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jdpgrailsdev.oasis.timeline.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.jdpgrailsdev.oasis.timeline.util.Generated;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/** Represents a timeline data event. */
@JsonInclude(Include.NON_NULL)
@SuppressWarnings({
  "PMD.DataClass",
  "PMD.CognitiveComplexity",
  "PMD.CyclomaticComplexity",
  "PMD.NPathComplexity"
})
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TimelineData {

  /*
   * N.B.: The order that the fields are declared in this class
   * must match the order as declared in the
   * src/main/resources/json/timelineData.json file.  This is
   * to ensure that any JSON generated from this object matches
   * the order of the incoming JSON file used to populate
   * this object.
   */

  private String description;

  private String date;

  private Boolean disputed;

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

  public Boolean isDisputed() {
    return disputed;
  }

  public void setDisputed(final Boolean disputed) {
    this.disputed = disputed;
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
    result = (prime * result) + ((date == null) ? 0 : date.hashCode());
    result = (prime * result) + ((description == null) ? 0 : description.hashCode());
    result = (prime * result) + ((disputed == null) ? 0 : disputed.hashCode());
    result = (prime * result) + ((source == null) ? 0 : source.hashCode());
    result = (prime * result) + ((title == null) ? 0 : title.hashCode());
    result = (prime * result) + ((type == null) ? 0 : type.hashCode());
    result = (prime * result) + ((year == null) ? 0 : year.hashCode());
    return result;
  }

  @Override
  @Generated
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TimelineData other = (TimelineData) obj;
    if (date == null) {
      if (other.date != null) {
        return false;
      }
    } else if (!date.equals(other.date)) {
      return false;
    }
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (disputed == null) {
      if (other.disputed != null) {
        return false;
      }
    } else if (!disputed.equals(other.disputed)) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    if (title == null) {
      if (other.title != null) {
        return false;
      }
    } else if (!title.equals(other.title)) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    if (year == null) {
      if (other.year != null) {
        return false;
      }
    } else if (!year.equals(other.year)) {
      return false;
    }
    return true;
  }

  @Override
  @Generated
  public String toString() {
    return "TimelineData [date="
        + date
        + ", description="
        + description
        + ", disputed="
        + disputed
        + ", source="
        + source
        + ", title="
        + title
        + ", type="
        + type
        + ", year="
        + year
        + "]";
  }
}
