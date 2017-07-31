package com.kristofdan.tlog16rs.core.beans;;

import java.time.LocalTime;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;
import com.kristofdan.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.kristofdan.tlog16rs.core.exceptions.NotExpectedTimeOrder;

public class UtilTest {
    
    Task newTask;
    LinkedList<Task> taskList;
    
    public UtilTest() {
    }

    @Test
    public void testCase1() throws Exception {
        LocalTime startTime = LocalTime.of(7, 30);
        LocalTime endTime = LocalTime.of(7, 50);
        
        LocalTime time = Util.roundToMultipleQuarterHour(startTime, endTime);
        
        assertEquals(LocalTime.of(7, 45), time);
    }
    
    @Test
    public void testCase2() throws Exception {
        LocalTime startTime = LocalTime.of(7, 30);
        LocalTime endTime = LocalTime.of(7, 50);
        
        boolean isMultipleQuarterHour = Util.isMultipleQuarterHour(startTime, endTime);
        
        assertEquals(false, isMultipleQuarterHour);
    }
    
    @Test
    public void testCase3() throws Exception {
        LocalTime startTime = LocalTime.of(7, 30);
        LocalTime endTime = LocalTime.of(7, 45);
        
        boolean isMultipleQuarterHour = Util.isMultipleQuarterHour(startTime, endTime);
        
        assertEquals(true, isMultipleQuarterHour);
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testCase4() throws Exception {
        Util.isMultipleQuarterHour(null, LocalTime.of(7, 45));
    }
    
    @Test(expected = NotExpectedTimeOrder.class)
    public void testCase5() throws Exception {
        LocalTime startTime = LocalTime.of(8, 30);
        LocalTime endTime = LocalTime.of(7, 45);
        Util.isMultipleQuarterHour(startTime, endTime);
    }
    
    @Test
    public void testCase6_1() throws Exception {
        createTaskListWithTimeInterval("06:30","06:45");
        createTaskWithTimeInterval("05:30","06:30");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(true, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_2() throws Exception {
        createTaskListWithTimeInterval("06:30","06:45");
        createTaskWithTimeInterval("06:45","07:00");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(true, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_3() throws Exception {
        createTaskListWithTimeInterval("06:30","06:30");
        createTaskWithTimeInterval("05:30","06:30");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(true, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_4() throws Exception {
        createTaskListWithTimeInterval("06:30","07:30");
        createTaskWithTimeInterval("07:30","07:30");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(true, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_5() throws Exception {
        createTaskListWithTimeInterval("06:30","07:00");
        createTaskWithTimeInterval("06:00","06:45");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(false, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_6() throws Exception {
        createTaskListWithTimeInterval("06:30","07:00");
        createTaskWithTimeInterval("06:30","06:45");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(false, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_7() throws Exception {
        createTaskListWithTimeInterval("06:30","07:00");
        createTaskWithTimeInterval("06:45","07:15");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(false, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_8() throws Exception {
        createTaskListWithTimeInterval("06:30","07:00");
        createTaskWithTimeInterval("06:45","07:00");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(false, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_9() throws Exception {
        createTaskListWithTimeInterval("06:30","06:30");
        createTaskWithTimeInterval("06:30","07:00");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(false, newTaskIsSeparatedTime);
    }
    
    @Test
    public void testCase6_10() throws Exception {
        createTaskListWithTimeInterval("06:30","07:30");
        createTaskWithTimeInterval("06:30","06:30");
        
        boolean newTaskIsSeparatedTime = Util.isSeparatedTime(newTask, taskList);
        
        assertEquals(false, newTaskIsSeparatedTime);
    }
    
    private void createTaskListWithTimeInterval(String startTime, String endTime) throws Exception{
        Task t = new Task("1234","",startTime, endTime);
        taskList = new LinkedList<>();
        taskList.add(t);
    }
    
    private void createTaskWithTimeInterval(String startTime, String endTime) throws Exception{
        newTask = new Task("1234","",startTime, endTime);
    }
}
