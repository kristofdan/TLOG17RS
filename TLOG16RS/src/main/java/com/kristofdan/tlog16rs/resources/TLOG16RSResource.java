package com.kristofdan.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import javax.ws.rs.*;
import com.kristofdan.tlog16rs.core.beans.*;
import static com.kristofdan.tlog16rs.core.beans.Service.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * Uses auxiliary methods from the Service class.
 * 
 * @author kristof
 */

@Path("/timelogger")
@Slf4j
public class TLOG16RSResource {
    
    private TimeLogger logger;

    public TLOG16RSResource() {
        logger = Ebean.find(TimeLogger.class).findUnique();
        if (logger == null){
            logger = new TimeLogger("Kristóf Dan");
        }
    }
    
    @Path("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkMonth> listEverything(){
        return Ebean.find(WorkMonth.class).findList();
    }
    
    @Path("/workmonths")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkMonth addNewMonth(WorkMonthRB inputMonth)
        throws Exception
    {
        try {
            WorkMonth newMonth = new WorkMonth(inputMonth.getYear(), inputMonth.getMonth());
            logger.addMonth(newMonth);
            
            Ebean.save(logger);
            return newMonth;
        } catch (Exception e) {
            log.error("Exception in method addNewMonth",e);
            return null;
        }
    }
    
    @Path("/workmonths/workdays")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewWorkDay(WorkDayRB inputDay)
        throws Exception
    {
        try {
            WorkDay newDay = Service.addDayAndCreateMonthIfNecessary(logger, inputDay);
            
            Ebean.save(logger);
            return newDay;
        } catch (Exception e) {
            log.error("Exception in method addNewWorkDay", e);
            return null;
        }
        
    }
    
    @Path("/workmonths/workdays/tasks/start")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task addNewTaskWithStartTime(StartTaskRB inputTask)
        throws Exception
    {
        try {
            Task task = new Task(inputTask.getTaskID(), inputTask.getComment(),
                inputTask.getStartTime(), inputTask.getStartTime());
            LocalDate date = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                inputTask.getDay());
            WorkDay dayOfNewTask = createOrGetDayForDate(date, logger);
            dayOfNewTask.addTask(task);
            
            Ebean.save(logger);
            return task;
        } catch (Exception e) {
            log.error("Exception in method addNewTaskWithStartTime", e);
            return null;
        }
        
    }
    
    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkDay> getMonthWithDate(@PathParam("year") int year, @PathParam("month") int month)
        throws Exception
    {
        try {
            String dateOfMonth = LocalDate.of(year, month, 1).toString();
            boolean monthNotExists = Ebean.find(WorkMonth.class).
                    where().eq("date", dateOfMonth).findUnique()
                    == null;
            if (monthNotExists){
                logger.addMonth(new WorkMonth(year, month));
                Ebean.save(logger);
                return new LinkedList<>();
            }else {
                return Ebean.find(WorkDay.class).fetch("workMonth").
                    where().eq("date", dateOfMonth).findList();
            }
        } catch (Exception e) {
            log.error("Exception in method getMonthWithDate", e);
            return null;
        }
        
    }
    
    @Path("workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getDayWithDate(@PathParam("year") int year, @PathParam("month") int month,
            @PathParam("day") int day)
        throws Exception
    {
        try {
            String dateOfDay = LocalDate.of(year, month, day).toString();
            String dateOfMonth = LocalDate.of(year, month, 1).toString();
            
            boolean dayExists = Ebean.find(WorkDay.class).
                    where().eq("actual_day", dateOfDay).findUnique()
                    != null;
            boolean monthExists = Ebean.find(WorkMonth.class).
                    where().eq("date", dateOfMonth).findUnique()
                    != null;
            
            if (dayExists){
                return Ebean.find(Task.class).fetch("workDay","actualDay").
                    where().eq("actual_day",dateOfDay).findList();
            }else if(monthExists){
                logger.getMonthWithDate(year, month).addWorkDay(new WorkDay(year, month, day));
                Ebean.save(logger);
                return new LinkedList<>();
            }else {
                WorkMonth newMonth = new WorkMonth(year, month);
                newMonth.addWorkDay(new WorkDay(year, month, day));
                logger.addMonth(newMonth);
                
                Ebean.save(logger);
                return new LinkedList<>();
            }
            
            
        } catch (Exception e) {
            log.error("Exception in method getDayWithDate", e);
            return null;
        }
        
    }
    
    @Path("workmonths/workdays/tasks/finish")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task finishTask(FinishingTaskRB inputTask)
        throws Exception
    {
        try {
            Task taskToFinish = new Task(inputTask.getTaskID(), "",
                inputTask.getStartTime(), inputTask.getEndTime());
            LocalDate dateOfTask = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                    inputTask.getDay());
            WorkDay dayOfNewTask = createOrGetDayForDate(dateOfTask, logger);
            Task finishedTask = dayOfNewTask.createOrFinishTask(taskToFinish, inputTask.getEndTime());
            updateStatisticsForMonthContainingDay(dayOfNewTask, logger);
            
            Ebean.save(logger);
            return finishedTask;
        } catch (Exception e) {
            log.error("Exception in method finishTask", e);
            return null;
        }
    }
    
    @Path("workmonths/workdays/tasks/modify")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task modifyTask(ModifyTaskRB inputTask)
        throws Exception
    {
        try {
            LocalDate dateOfTask = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                inputTask.getDay());
            WorkDay dayOfTask = createOrGetDayForDate(dateOfTask, logger);
            Task taskToModify = new Task(inputTask.getTaskId(),"",
                    inputTask.getStartTime(),inputTask.getStartTime());
            Task newValues = new Task(inputTask.getNewTaskId(),inputTask.getNewComment(),
                    inputTask.getNewStartTime(),inputTask.getNewEndTime());
            Task modifiedTask = dayOfTask.createOrModifyTask(taskToModify, newValues);
            updateStatisticsForMonthContainingDay(dayOfTask, logger);
            
            Ebean.save(logger);
            return modifiedTask;
        } catch (Exception e) {
            log.error("Exception in method modifyTask", e);
            return null;
        }
    }
    
    @Path("/workmonths/workdays/tasks/delete")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void deleteTask(DeleteTaskRB inputTask)
        throws Exception
    {
        try {
            WorkDay dayOfNewTask = new WorkDay(inputTask.getYear(),
                inputTask.getMonth(),inputTask.getDay());
            if (!logger.isNewDay(dayOfNewTask)) {
                Task taskToDelete = new Task(inputTask.getTaskId(), "",
                        inputTask.getStartTime(), inputTask.getStartTime());
                Task deletedTask = logger.getLastFoundDay().deleteTask(taskToDelete);
                updateStatisticsForMonthContainingDay(dayOfNewTask, logger);
                
                Ebean.delete(deletedTask);
                Ebean.save(logger);
            }
        } catch (Exception e) {
            log.error("Exception in method deleteTask", e);
        }
    }
    
    @Path("/workmonths/deleteall")
    @PUT
    public void deleteAllMonths(DeleteTaskRB inputTask){
        Ebean.deleteAll(logger.getMonths());
        logger.getMonths().clear();
    }
}