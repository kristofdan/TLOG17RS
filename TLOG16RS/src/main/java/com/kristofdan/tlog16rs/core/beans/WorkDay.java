package com.kristofdan.tlog16rs.core.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;
import java.time.*;
import com.kristofdan.tlog16rs.core.exceptions.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * A workday is represented by the tasks within it, it's date, the reqired working minutes and the sum of
 * it's tasks length.
 * 
 * @author Kristóf Dan
 */

@lombok.Getter
@Entity
public class WorkDay {
    @OneToMany(mappedBy = "workDay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;
    
    private LocalDate actualDay;
    private long requiredMinPerDay;
    private long sumPerDay;
    private long extraMinPerDay;
    
    @Id
    @GeneratedValue
    @JsonIgnore
    Integer id;
    @JsonIgnore
    @ManyToOne
    private WorkMonth workMonth;

//4 constructors for default arguments, default requiredMinPerDay: 450, actualDay: today
    /**
     * Requred minutes will be 450, the date: today. 
     */
    public WorkDay()
        throws Exception
    {
        this(450, LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
    }
    
    /**
     * Creates a WorkDay if requiredMinPerDay isn't negative.
     * The date will be today.
     */
    public WorkDay(long requiredMinPerDay)
        throws Exception
    {
        this(requiredMinPerDay, LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
    }
    
    /**
     * Creates a WorkDay if the date isn't later than today.
     * Required minutes will be 450.
     */
    public WorkDay(int year, int month, int day)
        throws Exception
    {
        this(450, year, month, day);
    }
    
    /**
     * Creates a WorkDay if requiredMinPerDay isn't negative and the date isn't later than today.
     */
    public WorkDay(long requiredMinPerDay, int year, int month, int day)
        throws Exception
    {
        if (requiredMinPerDay < 0)
        {
            throw new NegativeMinutesOfWorkException(
                    "Error: cannot construct day with negative required minutes");
        }else {
            this.requiredMinPerDay = requiredMinPerDay;
        }
        
        LocalDate newActualDay = LocalDate.of(year, month, day);
        if (newActualDay.isAfter(LocalDate.now())){
            throw new FutureWorkException("Error: cannot construct day with date later than today");
        }else {
            actualDay = newActualDay;
        }
        tasks = new LinkedList<>();
        sumPerDay = 0;
        extraMinPerDay = (-1) * requiredMinPerDay;
    }
    
    /**
     * Adds a task to the day if the task's time interval doesn't overlap with the
     * already added tasks intervals.
     */
    public void addTask(Task t)
        throws Exception
    {
        if (Util.isSeparatedTime(t,tasks)){
            tasks.add(t);
            sumPerDay += t.getMinPerTask();
            extraMinPerDay += t.getMinPerTask();
        }
        else {
            throw new NotSeparatedTimesException("Error: task cannot be added to day, it overlaps with other tasks within the day");
        }
    }
    
//REFAKTORÁLHATÓ!
    /**
     * Searches for the Task based on taskId and startTime.
     * Modifies the endTime if the task exists, otherwise inserts taskToFinish with the new endTime.
     */
    public Task createOrFinishTask(Task taskToFinish)
        throws Exception
    {
        for (Task currentTask : tasks) {
            if (currentTask.getTaskId().equals(taskToFinish.getTaskId()) &&
                    currentTask.getStartTime().equals(taskToFinish.getStartTime())){
                updateStatistics(currentTask, taskToFinish);
                currentTask.setEndTime(taskToFinish.getEndTime().toString());
                return currentTask;
            }
        }
        addTask(taskToFinish);
        return taskToFinish;
    }
    
    
//REFAKTORÁLHATÓ!
    /**
     * Searches for the task based on taskId and startTime.
     * Modifies the task if it exists, otherwise creates it.
     */
    public Task createOrModifyTask(Task taskToModify, Task newValues)
        throws Exception
    {
        for (Task currentTask : tasks) {
            if (currentTask.getTaskId().equals(taskToModify.getTaskId()) &&
                    currentTask.getStartTime().equals(taskToModify.getStartTime())){
                updateStatistics(currentTask, newValues);
                modifyTask(currentTask, newValues);
                return currentTask;
            }
        }
        addTask(newValues);
        return newValues;
    }
    
    private void updateStatistics(Task taskToModify, Task newValues)
        throws Exception
    {
        LocalTime newStartTime = newValues.getStartTime();
        LocalTime newEndTime = newValues.getEndTime();
        long minPerTaskDifference = Util.minPerTask(newStartTime, newEndTime) -
                taskToModify.getMinPerTask();
        sumPerDay += minPerTaskDifference;
        extraMinPerDay += minPerTaskDifference;
    }
    
    private void modifyTask(Task task, Task newValues)
        throws Exception
    {
        task.setTaskId(newValues.getTaskId());
        task.setComment(newValues.getComment());
        task.setStartTimeWithoutChecks(newValues.getStartTime().toString());
        task.setEndTime(newValues.getEndTime().toString());
    }
    
    /**
     * Searches for the Task based on taskId and startTime only.
     */
    public Task deleteTask(Task taskToDelete)
        throws Exception
    {
        for (Task currentTask : tasks) {
            if (currentTask.getTaskId().equals(taskToDelete.getTaskId()) &&
                    currentTask.getStartTime().equals(taskToDelete.getStartTime())){
                sumPerDay -= currentTask.getMinPerTask();
                extraMinPerDay -= currentTask.getMinPerTask();
                tasks.remove(currentTask);
                return currentTask;
            }
        }
        return null;
    }
    
    /**
     * If there are no tasks, returns 00:00.
     */
    //DIfferent from requested: If there are no tasks, returns 00:00 (more convenient to use)
    @Transient
    public LocalTime getLatestEndTime()
        throws Exception
    {
        if (tasks.isEmpty()){
            return LocalTime.of(0, 0);
        }else {
            LocalTime max = tasks.get(0).getEndTime();
            for (Task task : tasks){
                if (task.getEndTime().compareTo(max) > 0){
                    max = task.getEndTime();
                }
            }
            return max;
        }
    }
    
    /**
     * The must not be later than today.
     */
    public void setActualDay(int year, int month, int day) 
        throws Exception
    {
        LocalDate newActualDay = LocalDate.of(year, month, day);
        if (newActualDay.isAfter(LocalDate.now())){
            throw new FutureWorkException("Error: cannot set a day's date to a date later than today");
        }else {
            actualDay = newActualDay;
        }
    }
    
    /**
     * Must not be negative.
     */
    public void setRequiredMinPerDay(long requiredMinPerDay)
        throws Exception
    {
        if (requiredMinPerDay < 0)
        {
            throw new NegativeMinutesOfWorkException(
                    "Error: cannot set a day's reuired minutes to negative value");
        }else { 
            this.requiredMinPerDay = requiredMinPerDay;
        }
    }
    
    
}
