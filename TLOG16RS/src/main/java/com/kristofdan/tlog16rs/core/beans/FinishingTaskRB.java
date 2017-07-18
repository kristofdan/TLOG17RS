package com.kristofdan.tlog16rs.core.beans;

import lombok.*;

/**
 *
 * @author kristof
 */

@Getter
@Setter
@NoArgsConstructor
public class FinishingTaskRB {
    int year;
    int month;
    int day;
    String taskID;
    String startTime;
    String endTime;
}
