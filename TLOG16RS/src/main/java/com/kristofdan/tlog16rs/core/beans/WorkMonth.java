package com.kristofdan.tlog16rs.core.beans;

import java.util.*;
import java.time.*;
import com.kristofdan.tlog16rs.core.exceptions.*;

/**
 * A workmonth is represented by the days within it, it's date, the reqired working minutes and the sum of
 * it's tasks length.
 * 
 * @author Kristóf Dan
 */

@lombok.Getter
public class WorkMonth {
    private List<WorkDay> days;
    private YearMonth date;
    private long sumPerMonth;
    private long requiredMinPerMonth;
    private WorkDay lastFoundDay;

    public WorkMonth(int year, int month){
        date = YearMonth.of(year, month);
        days = new LinkedList<>();
    }
    
    /**
     *
     * The number of minutes that are above the required minutes. 
     */
    //Can return negative value
    public long getExtraMinPerMonth()
        throws Exception
    {
        calculateSumPerMonth();
        calculateRequiredMinPerMonth();
        return sumPerMonth - requiredMinPerMonth;
    }
    
    /**
     * The day must be a weekday.
     */
    public void addWorkDay(WorkDay wd)
        throws Exception
    {
        addWorkDay(wd, false);
    }
    
    public void addWorkDay(WorkDay wd, boolean isWeekendEnabled)
        throws Exception
    {
        if ((Util.isWeekday(wd.getActualDay()) || isWeekendEnabled) && isSameMonth(wd) && isNewDate(wd)){
            days.add(wd);
        }else {
            throwAppropriateException(wd,isWeekendEnabled);
        }
    }
    
    public boolean isNewDate(WorkDay dayToCompare){
        for (WorkDay day : days){
            if (dayToCompare.getActualDay().equals(day.getActualDay())) lastFoundDay = day; return false;
        }
        return true;
    }
    
    public boolean isSameMonth(WorkDay day){
        if (day.getActualDay().getYear() == date.getYear() &&
                day.getActualDay().getMonth() == date.getMonth()){
            return true;
        }
        else return false;
    }
    
    private void throwAppropriateException(WorkDay wd,boolean isWeekendEnabled)
       throws Exception
    {
       if (!Util.isWeekday(wd.getActualDay()) && !isWeekendEnabled){
           throw new WeekendNotEnabledException("Error: cannot add day to month, day is not weekday and weekend not enabled");
       }else if(!isSameMonth(wd)){
           throw new NotTheSameMonthException("Error: cannot add day to month, the day's date is in a different month");
       }else {
           throw new NotNewDateException("Error: cannot add day to month, a day with this date already exists");
       }
    }
    
    private void calculateSumPerMonth()
        throws Exception
    {
        sumPerMonth = 0;
        for (WorkDay currentDay : days) {
            sumPerMonth += currentDay.getSumPerDay();
        }
    }
    
    private void calculateRequiredMinPerMonth(){
        requiredMinPerMonth = 0;
        for (WorkDay currentDay : days) {
            requiredMinPerMonth += currentDay.getRequiredMinPerDay();
        }
    }
    
    public WorkDay getExistingDayWithDate(LocalDate date){
        for (WorkDay currentDay : days) {
            if (currentDay.getActualDay().equals(date)){
                return currentDay;
            }
        }
        return null;
    }

    public long getSumPerMonth()
        throws Exception
    {
        calculateSumPerMonth();
        return sumPerMonth;
    }

    public long getRequiredMinPerMonth() {
        calculateRequiredMinPerMonth();
        return requiredMinPerMonth;
    }
}
