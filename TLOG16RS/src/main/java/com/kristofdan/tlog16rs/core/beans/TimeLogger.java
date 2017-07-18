package com.kristofdan.tlog16rs.core.beans;

import java.util.*;
import com.kristofdan.tlog16rs.core.exceptions.NotNewMonthException;

/**
 * A timelogger is represented by the WorkMonth's within it.
 * 
 * @author Kristóf Dan
 */

@lombok.Getter

public class TimeLogger{
    private List<WorkMonth> months;
    private WorkMonth lastFoundMonth;
    private WorkDay lastFoundDay;

    public TimeLogger() {
        months = new LinkedList<>();
    }
    
    public void addMonth(WorkMonth month)
        throws Exception
    {
        if (isNewMonth(month)) months.add(month);
        else throw new NotNewMonthException("Error: cannot add this month to logger, a month with this date already exists");
    }
    
    /**
     * If the month already exists, saves it to the lastFoundMonth field.
     */
    public boolean isNewMonth(WorkMonth monthToCompare){
        for(WorkMonth currentMonth : months){
            if (monthToCompare.getDate().equals(currentMonth.getDate())){
                lastFoundMonth = currentMonth;
                return false;
            }
        }
        return true;
    }
    
    /**
     * If the month already exists, saves it to the lastFoundMonth field.
     */
    public boolean isNewMonth(int year, int month){
        WorkMonth monthToCompare = new WorkMonth(year, month);
        return isNewMonth(monthToCompare);
    }
    
    /**
     * If the month or the day already exists, saves them to the lastFoundMonth
     * and lastFoundDay fields.
     */
    public boolean isNewDay(WorkDay dayToCompare){
        WorkMonth monthOfDay = new WorkMonth(dayToCompare.getActualDay().getYear(),
                                            dayToCompare.getActualDay().getMonthValue());
        if(isNewMonth(monthOfDay)) return true;
        else {
            if (lastFoundMonth.isNewDate(dayToCompare)){
                return true;
            }else {
                lastFoundDay = lastFoundMonth.getLastFoundDay();
                return false;
            }
        }
    }
    
    public WorkMonth getExistingMonthOfGivenDay(WorkDay day){
        for (WorkMonth currentMonth : months) {
            if (currentMonth.isSameMonth(day)){
                return currentMonth;
            }
        }
        return null;
    }
    
    public void addNewDayToAppropriateExistingMonth(WorkDay newDay)
        throws Exception
    {
        for (WorkMonth currentMonth : months) {
            if (currentMonth.isSameMonth(newDay)){
                currentMonth.addWorkDay(newDay);
                return;
            }
        }
    }
    
}