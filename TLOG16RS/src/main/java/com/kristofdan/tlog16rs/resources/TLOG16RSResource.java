package com.kristofdan.tlog16rs.resources;

import javax.ws.rs.*;
import com.kristofdan.tlog16rs.core.beans.*;
import static com.kristofdan.tlog16rs.core.beans.Service.*;
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
        WorkMonth newMonth = new WorkMonth(inputMonth.getYear(), inputMonth.getMonth());
        logger.addMonth(newMonth);
        return newMonth;
    }
    
    @Path("/workmonths/workdays")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewWorkDay(WorkDayRB inputDay)
        throws Exception
    {
        WorkMonth monthOfNewDay = new WorkMonth(inputDay.getYear(), inputDay.getMonth());
        return Service.addDayAndCreateMonthIfNecessary(logger, inputDay);
    }
    
    @Path("/workmonths/workdays/tasks/start")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task addNewTaskWithStartTime(StartTaskRB inputTask)
        throws Exception
    {
        Task task = new Task(inputTask.getTaskID(), inputTask.getComment(),
                inputTask.getStartTime(), inputTask.getStartTime());
        LocalDate date = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                inputTask.getDay());
        WorkDay dayOfNewTask = createOrGetDayForDate(date, logger);
        dayOfNewTask.addTask(task);
        return task;
    }
    
    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkDay> getMonthWithDate(@PathParam("year") int year, @PathParam("month") int month)
        throws Exception
    {
        WorkMonth inputMonth = new WorkMonth(year, month);
        return Service.addOrGetMonth(inputMonth, logger).getDays();
    }
    
    @Path("workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getDayWithDate(@PathParam("year") int year, @PathParam("month") int month,
            @PathParam("day") int day)
        throws Exception
    {
        WorkMonth inputMonth = new WorkMonth(year, month);
        WorkDay inputDay = new WorkDay(year, month, day);
        WorkMonth monthOfTheDay = Service.addOrGetMonth(inputMonth, logger);
        
        return Service.addOrGetDay(monthOfTheDay,inputDay).getTasks();
    }
    
    @Path("workmonths/workdays/tasks/finish")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task finishTask(FinishingTaskRB inputTask)
        throws Exception
    {
        Task task = new Task(inputTask.getTaskID(), "",
                inputTask.getStartTime(), inputTask.getEndTime());
        LocalDate date = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                inputTask.getDay());
        WorkDay dayOfNewTask = createOrGetDayForDate(date, logger);
        return Service.createOrFinishTaskForGivenDay(dayOfNewTask, task);
    }
    
    @Path("workmonths/workdays/tasks/modify")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task modifyTask(ModifyTaskRB inputTask)
        throws Exception
    {
        LocalDate date = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                inputTask.getDay());
        WorkDay dayOfNewTask = createOrGetDayForDate(date, logger);
        return Service.createOrModifyTaskForGivenDay(dayOfNewTask, inputTask);
    }
    
    @Path("/workmonths/workdays/tasks/delete")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void deleteTask(DeleteTaskRB inputTask)
        throws Exception
    {
        WorkDay dayOfNewTask = new WorkDay(inputTask.getYear(),
                inputTask.getMonth(),inputTask.getDay());
        if (!logger.isNewDay(dayOfNewTask)) {
            Service.deleteTaskFromGivenDay(logger.getLastFoundDay(), inputTask);
        }
    }
    
    @Path("/workmonths/deleteall")
    @PUT
    public void deleteAllMonths(DeleteTaskRB inputTask)
        throws Exception
    {
        logger.getMonths().clear();
    }
}