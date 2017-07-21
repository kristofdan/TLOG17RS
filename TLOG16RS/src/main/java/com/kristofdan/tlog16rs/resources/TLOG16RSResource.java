package com.kristofdan.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import javax.ws.rs.*;
import com.kristofdan.tlog16rs.core.beans.*;
import static com.kristofdan.tlog16rs.core.beans.Service.*;
import com.kristofdan.tlog16rs.entities.TestEntity;
import java.time.LocalDate;
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
    
    private TimeLogger logger = new TimeLogger();
    
    @Path("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkMonth> listEverything(){
        return logger.getMonths();
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
            WorkMonth monthOfNewDay = new WorkMonth(inputDay.getYear(), inputDay.getMonth());
            return Service.addDayAndCreateMonthIfNecessary(logger, inputDay);
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
            WorkMonth inputMonth = new WorkMonth(year, month);
            return Service.addOrGetMonth(inputMonth, logger).getDays();
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
            WorkMonth inputMonth = new WorkMonth(year, month);
            WorkDay inputDay = new WorkDay(year, month, day);
            WorkMonth monthOfTheDay = Service.addOrGetMonth(inputMonth, logger);
            return Service.addOrGetDay(monthOfTheDay,inputDay).getTasks();
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
            Task task = new Task(inputTask.getTaskID(), "",
                inputTask.getStartTime(), inputTask.getEndTime());
            LocalDate date = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                    inputTask.getDay());
            WorkDay dayOfNewTask = createOrGetDayForDate(date, logger);
            return Service.createOrFinishTaskForGivenDay(dayOfNewTask, task);
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
            LocalDate date = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                inputTask.getDay());
            WorkDay dayOfNewTask = createOrGetDayForDate(date, logger);
            return Service.createOrModifyTaskForGivenDay(dayOfNewTask, inputTask);
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
                Service.deleteTaskFromGivenDay(logger.getLastFoundDay(), inputTask);
            }
        } catch (Exception e) {
            log.error("Exception in method deleteTask", e);
        }
    }
    
    @Path("/workmonths/deleteall")
    @PUT
    public void deleteAllMonths(DeleteTaskRB inputTask)
    {
        logger.getMonths().clear();
    }
    
    @Path("save/test")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String saveText(String text){
        TestEntity testEntity = new TestEntity();
        testEntity.setText(text);
        Ebean.save(testEntity);
        return text;
    }
}