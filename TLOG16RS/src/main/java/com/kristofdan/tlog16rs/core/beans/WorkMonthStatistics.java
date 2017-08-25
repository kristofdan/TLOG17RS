package com.kristofdan.tlog16rs.core.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Used for sending a WorkMonth's statistics.
 * 
 * @author kristof
 */

@Getter
@Setter
@NoArgsConstructor
public class WorkMonthStatistics {
    private long sumPerMonth;
    private long requiredMinPerMonth;
    private long extraMinPerMonth;

    public WorkMonthStatistics(long sumPerMonth, long requiredMinPerMonth, long extraMinPerMonth) {
        this.sumPerMonth = sumPerMonth;
        this.requiredMinPerMonth = requiredMinPerMonth;
        this.extraMinPerMonth = extraMinPerMonth;
    }
    
}
