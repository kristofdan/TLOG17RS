package com.kristofdan.tlog16rs.core.beans;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.Test;
import static org.junit.Assert.*;
import com.kristofdan.tlog16rs.core.exceptions.*;

public class WorkDayTest {
    
    public WorkDayTest() {
    }

    @Test
    public void testCase1() throws Exception {
        Task t = new Task("1234","","07:30","08:45");
        WorkDay wd = new WorkDay();
        wd.addTask(t);
        assertEquals(-375, wd.getExtraMinPerDay());
    }
    
    @Test
    public void testCase2() throws Exception {
        WorkDay wd = new WorkDay(321);
        assertEquals(-321, wd.getExtraMinPerDay());
    }
    
    @Test(expected = NegativeMinutesOfWorkException.class)
    public void testCase3() throws Exception {
        WorkDay wd = new WorkDay();
        wd.setRequiredMinPerDay(-321);
    }
    
    @Test(expected = NegativeMinutesOfWorkException.class)
    public void testCase4() throws Exception {
        WorkDay wd = new WorkDay(-321);
    }
    
    @Test(expected = FutureWorkException.class)
    public void testCase5() throws Exception {
        WorkDay wd = new WorkDay();
        LocalDate futureDate = LocalDate.now().plusDays(10);
        wd.setActualDay(futureDate.getYear(),
                futureDate.getMonthValue(),
                futureDate.getDayOfMonth());
    }
    
    @Test(expected = FutureWorkException.class)
    public void testCase6() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        WorkDay wd = new WorkDay(200, 
                futureDate.getYear(),
                futureDate.getMonthValue(),
                futureDate.getDayOfMonth());
    }
    
    @Test
    public void testCase7() throws Exception {
        Task t1 = new Task("1234","","07:30","08:45");
        Task t2 = new Task("1234","","08:45","09:45");
        WorkDay wd = new WorkDay();
        wd.addTask(t1);
        wd.addTask(t2);
        assertEquals(135, wd.getSumPerDay());
    }
    
    @Test
    public void testCase8() throws Exception {
        WorkDay wd = new WorkDay();
        assertEquals(0, wd.getSumPerDay());
    }
    
    @Test
    public void testCase9() throws Exception {
        Task t1 = new Task("1234","","07:30","08:45");
        Task t2 = new Task("1234","","09:30","11:45");
        WorkDay wd = new WorkDay();
        wd.addTask(t1);
        wd.addTask(t2);
        assertEquals(LocalTime.of(11, 45), wd.getLatestEndTime());
    }
    
    @Test
    public void testCase10() throws Exception {
        WorkDay wd = new WorkDay();
        assertEquals(LocalTime.of(0, 0), wd.getLatestEndTime());
    }
    
    @Test(expected = NotSeparatedTimesException.class)
    public void testCase11() throws Exception {
        Task t1 = new Task("1234","","07:30","08:45");
        Task t2 = new Task("1234","","08:30","09:45");
        WorkDay wd = new WorkDay();
        wd.addTask(t1);
        wd.addTask(t2);
    }
    
    @Test
    public void testCase12() throws Exception {
        WorkDay wd = new WorkDay(400, 1995, 10, 5);
        assertEquals(400, wd.getRequiredMinPerDay());
        assertEquals(LocalDate.of(1995, 10, 5), wd.getActualDay());
    }
    
    @Test
    public void testCase13() throws Exception {
        WorkDay wd = new WorkDay(1995, 10, 5);
        assertEquals(450, wd.getRequiredMinPerDay());
        assertEquals(LocalDate.of(1995, 10, 5), wd.getActualDay());
    }
    
    @Test
    public void testCase14() throws Exception {
        WorkDay wd = new WorkDay(300);
        assertEquals(300, wd.getRequiredMinPerDay());
        assertEquals(LocalDate.now(), wd.getActualDay());
    }
    
    @Test
    public void testCase15() throws Exception {
        WorkDay wd = new WorkDay();
        assertEquals(450, wd.getRequiredMinPerDay());
        assertEquals(LocalDate.now(), wd.getActualDay());
    }
    
    @Test
    public void testCase16() throws Exception {
        WorkDay wd = new WorkDay();
        wd.setActualDay(2016, 9, 1);
        assertEquals(LocalDate.of(2016, 9, 1), wd.getActualDay());
    }
    
    @Test
    public void testCase17() throws Exception {
        WorkDay wd = new WorkDay();
        wd.setRequiredMinPerDay(300);
        assertEquals(300, wd.getRequiredMinPerDay());
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testCase18() throws Exception {
        Task t = new Task("1234");
        WorkDay wd = new WorkDay();
        wd.addTask(t);
        wd.getSumPerDay();
    }
    
    @Test(expected = NotSeparatedTimesException.class)
    public void testCase19() throws Exception {
        Task t1 = new Task("1234","","08:45","09:50");
        Task t2 = new Task("2345","","08:20","08:45");
        WorkDay wd = new WorkDay();
        wd.addTask(t1);
        wd.addTask(t2);
    }
}
