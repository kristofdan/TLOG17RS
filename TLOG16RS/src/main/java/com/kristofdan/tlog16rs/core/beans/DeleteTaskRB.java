package com.kristofdan.tlog16rs.core.beans;

import lombok.*;

/**
 *
 * @author kristof
 */

@Getter
@Setter
@NoArgsConstructor
public class DeleteTaskRB {
    int year;
    int month;
    int day;
    String taskId;
    String startTime;
}
