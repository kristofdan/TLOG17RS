package com.kristofdan.tlog16rs.core.beans;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Auxiliary methods for the TLOG16Resource class.
 * 
 * @author kristof
 */

public class Service {
    
    public static void updateStatisticsForMonthContainingDay(WorkDay day, TimeLogger logger){
        YearMonth dateOfMonth = YearMonth.of(day.getActualDay().getYear(), day.getActualDay().getMonthValue());
        logger.updateStatisticsForMonth(dateOfMonth);
    }
    
    public static WorkMonth addOrGetMonth(WorkMonth month, TimeLogger logger)
        throws Exception
    {
        if (logger.isNewMonth(month)){
            logger.addMonth(month);
            return month;
        }else {
            return logger.getLastFoundMonth();
        }
    }
    
    public static WorkDay addOrGetDay(WorkMonth month, WorkDay day)
        throws Exception
    {
        for (WorkDay currentDay : month.getDays()) {
            if(currentDay.getActualDay().equals(day.getActualDay())){
                return currentDay;
            }
        }
        month.addWorkDay(day);
        return day;
    }
    
    public static WorkDay addDayAndCreateMonthIfNecessary(TimeLogger logger, WorkDay day)
        throws Exception
    {
        WorkMonth monthOfTheDay = new WorkMonth(day.getActualDay().getYear(),
                day.getActualDay().getMonthValue());
        monthOfTheDay = addOrGetMonth(monthOfTheDay, logger);
        monthOfTheDay.addWorkDay(day);
        return day;
    }
    
    public static WorkDay addDayAndCreateMonthIfNecessary(TimeLogger logger, WorkDayRB dayRB)
        throws Exception
    {
        WorkDay day = new WorkDay(dayRB.getRequiredMinPerDay(), dayRB.getYear(),
                dayRB.getMonth(), dayRB.getDay());
        return addDayAndCreateMonthIfNecessary(logger, day);
    }
    
    public static WorkDay createOrGetDayForDate(LocalDate date, TimeLogger logger)
        throws Exception
    {
        
        WorkDay dayOfNewTask = new WorkDay(date.getYear(),date.getMonthValue(),date.getDayOfMonth());
        WorkMonth monthOfNewTask = new WorkMonth(date.getYear(), date.getMonthValue());
        monthOfNewTask = addOrGetMonth(monthOfNewTask, logger);
        if (monthOfNewTask.isNewDate(dayOfNewTask)){
            monthOfNewTask.addWorkDay(dayOfNewTask);
        }else {
            dayOfNewTask = monthOfNewTask.getExistingDayWithDate(date);
        }
        return dayOfNewTask;
    }
    
    public static Task createOrFinishTaskForGivenDay(WorkDay day, Task task)
        throws Exception
    {
        for (Task currentTask : day.getTasks()) {
            if (currentTask.getTaskId().equals(task.getTaskId()) &&
                    currentTask.getStartTime().equals(task.getStartTime())){
                currentTask.setEndTime(task.getEndTime().toString());
                return currentTask;
            }
        }
        day.addTask(task);
        return task;
    }
}
