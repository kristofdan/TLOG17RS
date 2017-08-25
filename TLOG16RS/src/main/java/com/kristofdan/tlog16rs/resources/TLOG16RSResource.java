package com.kristofdan.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import javax.ws.rs.*;
import com.kristofdan.tlog16rs.core.beans.*;
import static com.kristofdan.tlog16rs.core.beans.Service.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    private Map<String,Integer> statusCodesForExceptions;

    public TLOG16RSResource() {
        logger = Ebean.find(TimeLogger.class).findUnique();
        if (logger == null){
            logger = new TimeLogger("Kristóf Dan");
        }
        
        createStatusCodes();
    }
    
    private void createStatusCodes(){
        statusCodesForExceptions = new HashMap<>();
        statusCodesForExceptions.put("EmptyTimeFieldException", 406);
        statusCodesForExceptions.put("FutureWorkException", 407);
        statusCodesForExceptions.put("InvalidTaskIdException", 408);
        statusCodesForExceptions.put("NegativeMinutesOfWorkException", 409);
        statusCodesForExceptions.put("NoTaskIdException", 410);
        statusCodesForExceptions.put("NotExpectedTimeOrder", 411);
        statusCodesForExceptions.put("NotNewDateException", 412);
        statusCodesForExceptions.put("NotNewMonthException", 413);
        statusCodesForExceptions.put("NotSeparatedTimesException", 414);
        statusCodesForExceptions.put("NotTheSameMonthException", 415);      //Cannot occur through the endpoints
        statusCodesForExceptions.put("WeekendNotEnabledException", 416);
    }
    
    @Path("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listEverything(){
        return Response.ok(Ebean.find(WorkMonth.class).findList()).build();
    }
    
    @Path("/workmonths")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewMonth(WorkMonthRB inputMonth){
        try {
            WorkMonth newMonth = new WorkMonth(inputMonth.getYear(), inputMonth.getMonth());
            logger.addMonth(newMonth);
            
            Ebean.save(logger);
            return Response.ok(newMonth).build();
        } catch (Exception e) {
            log.error("Exception in method addNewMonth",e);
            return createResponseForException(e);
        }
    }
    
    @Path("/workmonths/workdays")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewWeekdayWorkDay(WorkDayRB inputDay){
        return addNewWorkDay(inputDay, true);
    }
    
    @Path("/workmonths/workdays/weekend")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewWeekendWorkDay(WorkDayRB inputDay){
        return addNewWorkDay(inputDay, false);
    }
    
    private Response addNewWorkDay(WorkDayRB inputDay, boolean isWeekDay){
        try {
            WorkDay newDay = Service.addDayAndCreateMonthIfNecessary(logger, inputDay, isWeekDay);
            
            Ebean.save(logger);
            return Response.ok(newDay).build();
        } catch (Exception e) {
            log.error("Exception in method addNewWorkDay", e);
            return createResponseForException(e);
        }
    }
    
    @Path("/workmonths/workdays/tasks/start")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewTaskWithStartTime(StartTaskRB inputTask){
        try {
            Task task = new Task(inputTask.getTaskID(), inputTask.getComment(),
                inputTask.getStartTime(), inputTask.getStartTime());
            LocalDate date = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                inputTask.getDay());
            WorkDay dayOfNewTask = createOrGetDayForDate(date, logger);
            dayOfNewTask.addTask(task);
            
            Ebean.save(logger);
            return Response.ok(task).build();
        } catch (Exception e) {
            log.error("Exception in method addNewTaskWithStartTime", e);
            return createResponseForException(e);
        }
        
    }
    
    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthWithDate(@PathParam("year") int year, @PathParam("month") int month){
        try {
            String dateOfMonth = LocalDate.of(year, month, 1).toString();
            boolean monthNotExists = Ebean.find(WorkMonth.class).
                    where().eq("date", dateOfMonth).findUnique()
                    == null;
            if (monthNotExists){
                logger.addMonth(new WorkMonth(year, month));
                Ebean.save(logger);
                return Response.ok(new LinkedList<>()).build();
            }else {
                List<WorkDay> dayList = Ebean.find(WorkDay.class).fetch("workMonth").
                    where().eq("date", dateOfMonth).findList();
                return Response.ok(dayList).build();
            }
        } catch (Exception e) {
            log.error("Exception in method getMonthWithDate", e);
            return createResponseForException(e);
        }
        
    }
    
    @Path("workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDayWithDate(@PathParam("year") int year, @PathParam("month") int month,
            @PathParam("day") int day){
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
                List<Task> taskList = Ebean.find(Task.class).fetch("workDay","actualDay").
                    where().eq("actual_day",dateOfDay).findList();
                return Response.ok(taskList).build();
            }else if(monthExists){
                logger.getMonthWithDate(year, month).addWorkDay(new WorkDay(year, month, day));
                Ebean.save(logger);
                return Response.ok(new LinkedList<>()).build();
            }else {
                WorkMonth newMonth = new WorkMonth(year, month);
                newMonth.addWorkDay(new WorkDay(year, month, day));
                logger.addMonth(newMonth);
                
                Ebean.save(logger);
                return Response.ok(new LinkedList<>()).build();
            }
            
            
        } catch (Exception e) {
            log.error("Exception in method getDayWithDate", e);
            return createResponseForException(e);
        }
        
    }
    
    @Path("/workmonths/{year}/{month}/statistics")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatisticsForMonthWithDate(@PathParam("year") int year, @PathParam("month") int month){
        try {
            String dateOfMonth = LocalDate.of(year, month, 1).toString();
            WorkMonth requestedMonth = Ebean.find(WorkMonth.class).
                    where().eq("date", dateOfMonth).findUnique();
            if (requestedMonth == null){
                logger.addMonth(new WorkMonth(year, month));
                Ebean.save(logger);
                return Response.ok(new WorkMonthStatistics(0,0,0)).build();
            }else {
                WorkMonthStatistics statistics = new WorkMonthStatistics(requestedMonth.getSumPerMonth(),
                        requestedMonth.getRequiredMinPerMonth(),
                        requestedMonth.getExtraMinPerMonth());
                return Response.ok(statistics).build();
            }
        } catch (Exception e) {
            log.error("Exception in method getMonthWithDate", e);
            return createResponseForException(e);
        }
    }
    
    @Path("workmonths/workdays/tasks/finish")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response finishTask(FinishingTaskRB inputTask){
        try {
            Task taskToFinish = new Task(inputTask.getTaskID(), "",
                inputTask.getStartTime(), inputTask.getEndTime());
            LocalDate dateOfTask = LocalDate.of(inputTask.getYear(), inputTask.getMonth(),
                    inputTask.getDay());
            WorkDay dayOfNewTask = createOrGetDayForDate(dateOfTask, logger);
            Task finishedTask = dayOfNewTask.createOrFinishTask(taskToFinish);
            updateStatisticsForMonthContainingDay(dayOfNewTask, logger);
            
            Ebean.save(logger);
            return Response.ok(finishedTask).build();
        } catch (Exception e) {
            log.error("Exception in method finishTask", e);
            return createResponseForException(e);
        }
    }
    
    @Path("workmonths/workdays/tasks/modify")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyTask(ModifyTaskRB inputTask){
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
            return Response.ok(modifiedTask).build();
        } catch (Exception e) {
            log.error("Exception in method modifyTask", e);
            return createResponseForException(e);
        }
    }
    
    @Path("/workmonths/workdays/tasks/delete")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTask(DeleteTaskRB inputTask){
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
            return Response.noContent().build();
        } catch (Exception e) {
            log.error("Exception in method deleteTask", e);
            return createResponseForException(e);
        }
    }
    
    @Path("/workmonths/deleteall")
    @PUT
    public Response deleteAllMonths(DeleteTaskRB inputTask){
        Ebean.deleteAll(logger.getMonths());
        logger.getMonths().clear();
        return Response.noContent().build();
    }
    
    private Response createResponseForException(Exception e){
        String fullyQualifiedExceptionName = e.toString().substring(0,e.toString().indexOf(":"));
        String exceptionName = fullyQualifiedExceptionName.substring(
                fullyQualifiedExceptionName.lastIndexOf(".") + 1);
        int statusCode = statusCodesForExceptions.get(exceptionName);
        return Response.status(statusCode).build();
    }
}