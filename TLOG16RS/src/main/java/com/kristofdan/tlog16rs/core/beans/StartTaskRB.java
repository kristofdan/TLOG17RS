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
public class StartTaskRB {
    int year;
    int month;
    int day;
    String taskID;
    String startTime;
    String comment;
}
