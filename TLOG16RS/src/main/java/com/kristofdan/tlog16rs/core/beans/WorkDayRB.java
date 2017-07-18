package com.kristofdan.tlog16rs.core.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 *
 * @author kristof
 */

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class WorkDayRB {
    int requiredMinPerDay;
    int year;
    int month;
    int day;
}
