package com.kristofdan.tlog16rs.core.beans;

import java.time.*;
import java.util.*;
import java.time.temporal.ChronoField;
import com.kristofdan.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.kristofdan.tlog16rs.core.exceptions.NotExpectedTimeOrder;

/**
 * Utility functions for the non-IU classes of this package. 
 * 
 * @author Kristóf Dan
 */

public class Util {
    public static boolean isWeekday(LocalDate day){
        DayOfWeek dayOfWeek = day.getDayOfWeek();
        return (dayOfWeek != DayOfWeek.SATURDAY) && (dayOfWeek != DayOfWeek.SUNDAY);
    }
    
    public static boolean isMultipleQuarterHour(LocalTime startTime, LocalTime endTime)
        throws Exception
    {
        return (minPerTask(startTime, endTime) % 15 == 0);
    }
    
    public static boolean isSeparatedTime(Task taskToCompare, List<Task> groupOfTasks)
        throws Exception
    {
        for (Task currentTask : groupOfTasks) {
            boolean areOverlappingTasks = areOverlappingTasks(taskToCompare, currentTask);
            if (areOverlappingTasks){
                return false;
            }
        }
        return true;
    }
    
    private static boolean areOverlappingTasks(Task first, Task second)
        throws Exception
    {
        return startOfFirstTaskIsDuringSecond(first,second) || endOfFirstTaskIsDuringSecond(first, second);
    }
    
    //If a task's time interval if zero (meaning end time not specified yet), it's starttime
    //shuldn't be the same as the other's, because it's endtime will be a later time when specified
    private static boolean startOfFirstTaskIsDuringSecond(Task first, Task second)
        throws Exception
    {
        boolean startOfFirstIsLaterThanStartOfSecond =
                first.getStartTime().compareTo(second.getStartTime()) >= 0;
        boolean startOfFirstIsEarlierThanEndOfSecond =
                first.getStartTime().compareTo(second.getEndTime()) < 0;
        boolean secondIsZeroLength = Util.minPerTask(second.getStartTime(), second.getEndTime()) == 0;
        return (startOfFirstIsLaterThanStartOfSecond && startOfFirstIsEarlierThanEndOfSecond) ||
               (startOfFirstIsLaterThanStartOfSecond && secondIsZeroLength);
    }
    
    private static boolean endOfFirstTaskIsDuringSecond(Task first, Task second)
        throws Exception
    {
        boolean endOfFirstIsLaterThanStartOfSecond =
                first.getEndTime().compareTo(second.getStartTime()) > 0;      
        boolean endOfFirstIsEarlierThanEndOfSecond =
                first.getEndTime().compareTo(second.getEndTime()) < 0;
        return endOfFirstIsLaterThanStartOfSecond && endOfFirstIsEarlierThanEndOfSecond;
    }
    
    /**
     * @return The rounded endTime.
     */
    //EndTime can be rounded past midnight
    public static LocalTime roundToMultipleQuarterHour(LocalTime startTime, LocalTime endTime)
        throws Exception
    {
        long duration = minPerTask(startTime, endTime);
        long remainder = duration % 15;
        //zero length task means endtime not added yet
        boolean wouldBeRoundedToZeroLength = remainder<8 && remainder != 0 &&
                endTime.minusMinutes(remainder).equals(startTime);
        if (remainder<8 && !wouldBeRoundedToZeroLength){
            return endTime.minusMinutes(remainder);
        }else {
            return endTime.plusMinutes(15 - remainder);
        }
    }
    
    public static long minPerTask(LocalTime startTime, LocalTime endTime)
        throws Exception
    {
        if (startTime == null || endTime == null){
            throw new EmptyTimeFieldException("Error: missing time field");
        }
        if (startTime.compareTo(endTime) > 0){
            throw new NotExpectedTimeOrder("Error: not a proper time interval");
        }
        
        long hourDiff =
                endTime.getLong(ChronoField.HOUR_OF_DAY) - startTime.getLong(ChronoField.HOUR_OF_DAY);
        long minuteDiff =
                endTime.getLong(ChronoField.MINUTE_OF_HOUR) - startTime.getLong(ChronoField.MINUTE_OF_HOUR);
        return hourDiff * 60 + minuteDiff;
    }
}