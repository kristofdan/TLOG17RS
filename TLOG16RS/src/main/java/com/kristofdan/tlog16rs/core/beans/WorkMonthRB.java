package com.kristofdan.tlog16rs.core.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author kristof
 */

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkMonthRB {
    private int year;
    private int month;

}
