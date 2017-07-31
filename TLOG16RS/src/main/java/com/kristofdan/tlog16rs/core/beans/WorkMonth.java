package com.kristofdan.tlog16rs.core.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;
import java.time.*;
import com.kristofdan.tlog16rs.core.exceptions.*;
import javax.persistence.*;

/**
 * A workmonth is represented by the days within it, it's date, the reqired working minutes and the sum of
 * it's tasks length.
 * 
 * The statistics are only valid if updateStatistisc() was called since the last modification.
 * 
 * @author Kristóf Dan
 */

@lombok.Getter
@Entity
public class WorkMonth {
    
    @OneToMany(mappedBy = "workMonth", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkDay> days;

    private YearMonth date;
    private long sumPerMonth;
    private long requiredMinPerMonth;
    private long extraMinPerMonth;
    @JsonIgnore
    @Transient
    private WorkDay lastFoundDay;
    
    @Id
    @GeneratedValue
    @JsonIgnore
    Integer id;
    
    @JsonIgnore
    @ManyToOne
    private TimeLogger timeLogger;

    public WorkMonth(int year, int month){
        date = YearMonth.of(year, month);
        sumPerMonth = 0;
        requiredMinPerMonth = 0;
        extraMinPerMonth = 0;
        days = new LinkedList<>();
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
            if (dayToCompare.getActualDay().equals(day.getActualDay())){
                lastFoundDay = day;
                return false;
            }
        }
        return true;
    }
    
    public boolean isSameMonth(WorkDay day){
        return  day.getActualDay().getYear() == date.getYear() &&
                day.getActualDay().getMonth() == date.getMonth();
    }
    
    private void throwAppropriateException(WorkDay wd,boolean isWeekendEnabled)
       throws Exception
    {
       if (!Util.isWeekday(wd.getActualDay()) && !isWeekendEnabled){
           throw new WeekendNotEnabledException("Error: cannot add day to month, day is not weekday and weekend not enabled");
       }else if(!isSameMonth(wd)){
           throw new NotTheSameMonthException("Error: cannot add day to month, the day's date is in a different month");
       }else if(!isNewDate(wd)){
           throw new NotNewDateException("Error: cannot add day to month, a day with this date already exists");
       }
    }
    
    /**
     * Updates the fields containing statistics
     * (sumPerMonth, requiredMinPerMonth, extraMinPerMonth).
     */
    //Need to be called to the fields containing statistics be up to date
    public void updateStatistics(){
        sumPerMonth = 0;
        requiredMinPerMonth = 0;
        for (WorkDay currentDay : days) {
            sumPerMonth += currentDay.getSumPerDay();
            requiredMinPerMonth += currentDay.getRequiredMinPerDay();
        }
        extraMinPerMonth = sumPerMonth - requiredMinPerMonth;
    }
    
    public WorkDay getExistingDayWithDate(LocalDate date){
        for (WorkDay currentDay : days) {
            if (currentDay.getActualDay().equals(date)){
                return currentDay;
            }
        }
        return null;
    }
    
    /**
     * The number of minutes that are above the required minutes. 
     */
    public long getExtraMinPerMonth(){
        updateStatistics();
        return extraMinPerMonth;
    }

    public long getSumPerMonth(){
        updateStatistics();
        return sumPerMonth;
    }

    public long getRequiredMinPerMonth(){
        updateStatistics();
        return requiredMinPerMonth;
    }
    
    public void setDate(String date) {
        this.date = YearMonth.parse(date);
    }
}
