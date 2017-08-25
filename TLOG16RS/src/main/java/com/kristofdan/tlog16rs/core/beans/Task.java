package com.kristofdan.tlog16rs.core.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
import java.util.regex.*;
import lombok.Getter;
import lombok.Setter;
import com.kristofdan.tlog16rs.core.exceptions.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * A task is represented by a task ID, a description (comment), a start and an end time.
 * 
 * @author Kristóf Dan
 */

@Getter
@Setter
@Entity
public class Task {
    private String taskId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String comment;
    private long minPerTask;
    
    @Id
    @GeneratedValue
    @JsonIgnore
    Integer id;
    
    @ManyToOne
    @JsonIgnore
    private WorkDay workDay;
    
    /**
     * Constructs startTime and endTime from integer values.
     */
    public Task(String taskId, String comment, int startHour, int startMinute, int endHour, int endMinute)
        throws Exception
    {
        LocalTime startTimeInput = LocalTime.of(startHour, startMinute);
        LocalTime endTimeInput = LocalTime.of(endHour, endMinute);
        checkIfValidTimeOrder(startTimeInput,endTimeInput);
        setTaskIdIfValid(taskId);
        this.comment = comment;
        this.startTime = startTimeInput;
        setEndTimeSoThatDurationIsMultipleQuarterHour(startTimeInput,endTimeInput);
    }
    
    /**
     * Constructs startTime and endTime from Strings.
     */
    public Task(String taskId, String comment, String startTimeString, String endTimeString)
        throws Exception
    {
        LocalTime startTimeInput = Util.toLocalTime(startTimeString);
        LocalTime endTimeInput = Util.toLocalTime(endTimeString);
        checkIfValidTimeOrder(startTimeInput,endTimeInput);
        setTaskIdIfValid(taskId);
        this.comment = comment;
        this.startTime = startTimeInput;
        setEndTimeSoThatDurationIsMultipleQuarterHour(startTimeInput,endTimeInput);
    }

    public Task(String taskId) throws Exception {
        setTaskIdIfValid(taskId);

    }
    
    
    private void checkIfValidTimeOrder(LocalTime startTime, LocalTime endTime)
        throws Exception
    {
        if (startTime.compareTo(endTime) > 0){
            throw new NotExpectedTimeOrder("Error: Not a valid time interval");
        }
        
    }
    
    private void setTaskIdIfValid(String taskId)
        throws Exception
    {
        if (isValidTaskId(taskId)){
            this.taskId = taskId;
        }else if (taskId.equals("")){
            throw new NoTaskIdException("Error: missing task ID");
        }
        else {
            throw new InvalidTaskIdException("Error: invalid task ID");
        }
    }
    
    private void setEndTimeSoThatDurationIsMultipleQuarterHour(LocalTime startTime, LocalTime endTime)
        throws Exception
    {
        this.endTime = Util.roundToMultipleQuarterHour(startTime, endTime);
        minPerTask = calculateMinPerTask();
    }

    public long calculateMinPerTask() 
        throws Exception
    {
        if (startTime == null || endTime == null){
            throw new EmptyTimeFieldException("Error: time field missing from task");
        }else {
            return Util.minPerTask(startTime, endTime);
        }
    }
    
    public boolean isValidTaskId(String taskId){
        return isValidRedmineTaskId(taskId) || isValidLTTaskId(taskId);
    }
    
    private boolean isValidRedmineTaskId(String taskId){
        return Pattern.matches("^\\d\\d\\d\\d$", taskId);
    }
    
    private boolean isValidLTTaskId(String taskId){
        return Pattern.matches("^LT-\\d\\d\\d\\d$", taskId);
    }
    
    public String toString(){
        return taskId + " " + comment;
    }
    
    public LocalTime getStartTime() throws Exception {
        if (startTime == null || endTime == null){
            throw new EmptyTimeFieldException("Error: start time missing from task");
        }else {
            return startTime;
        }
    }
    
    public LocalTime getEndTime()
        throws Exception
    {
        if (startTime == null || endTime == null){
            throw new EmptyTimeFieldException("Error: end time missing from task");
        }else {
            return endTime;
        }
    }
    
    public long getMinPerTask()
        throws Exception
    {
        if (startTime == null || endTime == null){
            throw new EmptyTimeFieldException("Error: end time missing from task");
        }else {
            return minPerTask;
        }
    }
    
    public void setTaskId(String taskId)
        throws Exception
    {
        setTaskIdIfValid(taskId);
    }

    public void setStartTime(int hour, int minute)
        throws Exception 
    {
        LocalTime newStartTime = LocalTime.of(hour, minute);
        setStartTime(newStartTime);
    }
    
    public void setStartTime(String startTimeAsString)
        throws Exception
    {
        LocalTime newStartTime = Util.toLocalTime(startTimeAsString);
        setStartTime(newStartTime);
    }
    
    private void setStartTime(LocalTime newStartTime)
        throws Exception
    {
        checkIfValidTimeOrder(newStartTime,endTime);
        startTime = newStartTime;
        setEndTimeSoThatDurationIsMultipleQuarterHour(startTime, endTime);
    }
    
    /**
     * Doesn't check if startTime is earlier than endTime, and doesn't round the interval.
     * Doesn't update minPerTask, should be you used together with setEndTime.
     */
    public void setStartTimeWithoutChecks(String startTimeAsString){
        LocalTime newStartTime = Util.toLocalTime(startTimeAsString);
        startTime = newStartTime;
    }

    public void setEndTime(int hour, int minute)
        throws Exception
    {
        LocalTime newEndTime = LocalTime.of(hour, minute);
        checkIfValidTimeOrder(startTime,newEndTime);
        endTime = newEndTime;
        setEndTimeSoThatDurationIsMultipleQuarterHour(startTime, endTime);
    }
    
    public void setEndTime(String endTimeAsString)
        throws Exception
    {
        LocalTime newEndTime = Util.toLocalTime(endTimeAsString);
        checkIfValidTimeOrder(startTime,newEndTime);
        endTime = newEndTime;
        setEndTimeSoThatDurationIsMultipleQuarterHour(startTime, endTime);
    }
}