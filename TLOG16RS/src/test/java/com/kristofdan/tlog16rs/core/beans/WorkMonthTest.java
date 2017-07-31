package com.kristofdan.tlog16rs.core.beans;

import org.junit.Test;
import static org.junit.Assert.*;
import com.kristofdan.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.kristofdan.tlog16rs.core.exceptions.NotNewDateException;
import com.kristofdan.tlog16rs.core.exceptions.NotTheSameMonthException;
import com.kristofdan.tlog16rs.core.exceptions.WeekendNotEnabledException;

public class WorkMonthTest {
    
    public WorkMonthTest() {
    }

    @Test
    public void testCase1() throws Exception {
        Task t1 = new Task("1234","","07:30","08:45");
        WorkDay wd1 = new WorkDay(420,2016,9,2);
        Task t2 = new Task("2345","","08:45","09:45");
        WorkDay wd2 = new WorkDay(420,2016,9,1);
        WorkMonth wm = new WorkMonth(2016, 9);
        
        wd1.addTask(t1);
        wd2.addTask(t2);
        wm.addWorkDay(wd1);
        wm.addWorkDay(wd2);
        
        assertEquals(135, wm.getSumPerMonth());
    }
    
    @Test
    public void testCase2() throws Exception {
        WorkMonth wm = new WorkMonth(2016, 9);
        assertEquals(0, wm.getSumPerMonth());
    }
    
    @Test
    public void testCase3() throws Exception {
        Task t1 = new Task("1234","","07:30","08:45");
        WorkDay wd1 = new WorkDay(420,2016,9,2);
        Task t2 = new Task("2345","","08:45","09:45");
        WorkDay wd2 = new WorkDay(420,2016,9,1);
        WorkMonth wm = new WorkMonth(2016, 9);
        
        wd1.addTask(t1);
        wd2.addTask(t2);
        wm.addWorkDay(wd1);
        wm.addWorkDay(wd2);
        
        assertEquals(-705, wm.getExtraMinPerMonth());
    }
    
    @Test
    public void testCase4() throws Exception {
        WorkMonth wm = new WorkMonth(2016, 9);
        assertEquals(0, wm.getExtraMinPerMonth());
    }
    
    @Test
    public void testCase5() throws Exception {
        WorkDay wd1 = new WorkDay(420,2016,9,9);
        WorkDay wd2 = new WorkDay(420,2016,9,1);
        WorkMonth wm = new WorkMonth(2016, 9);
        
        wm.addWorkDay(wd1);
        wm.addWorkDay(wd2);
        
        assertEquals(840, wm.getRequiredMinPerMonth());
    }
    
    @Test
    public void testCase6() throws Exception{
        WorkMonth wm = new WorkMonth(2016, 9);
        assertEquals(0, wm.getRequiredMinPerMonth());
    }
    
    @Test
    public void testCase7() throws Exception {
        Task t = new Task("1234","","07:30","08:45");
        WorkDay wd = new WorkDay(2016,9,9);
        WorkMonth wm = new WorkMonth(2016, 9);
        
        wd.addTask(t);
        wm.addWorkDay(wd);
        
        assertEquals(wd.getSumPerDay(), wm.getSumPerMonth());
    }
    
    @Test
    public void testCase8() throws Exception {
        Task t = new Task("1234","","07:30","08:45");
        WorkDay wd = new WorkDay(2016,8,28);
        WorkMonth wm = new WorkMonth(2016, 8);
        
        wd.addTask(t);
        wm.addWorkDay(wd,true);
        
        assertEquals(wd.getSumPerDay(), wm.getSumPerMonth());
    }
    
    @Test(expected = WeekendNotEnabledException.class)
    public void testCase9() throws Exception {
        Task t = new Task("1234","","07:30","08:45");
        WorkDay wd = new WorkDay(2016,8,28);
        WorkMonth wm = new WorkMonth(2016, 8);
        
        wd.addTask(t);
        wm.addWorkDay(wd,false);
    }
    
    @Test(expected = NotNewDateException.class)
    public void testCase10() throws Exception {
        WorkDay wd1 = new WorkDay(2016,9,1);
        WorkDay wd2 = new WorkDay(2016,9,1);
        WorkMonth wm = new WorkMonth(2016, 9);
        
        wm.addWorkDay(wd1);
        wm.addWorkDay(wd2);
    }
    
    @Test(expected = NotTheSameMonthException.class)
    public void testCase11() throws Exception {
        WorkDay wd1 = new WorkDay(2016,9,1);
        WorkDay wd2 = new WorkDay(2016,8,30);
        WorkMonth wm = new WorkMonth(2016, 9);
        
        wm.addWorkDay(wd1);
        wm.addWorkDay(wd2);
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testCase12() throws Exception {
        Task t = new Task("1234");
        WorkDay wd = new WorkDay(2016, 9, 1);
        WorkMonth wm = new WorkMonth(2016, 9);
        
        wd.addTask(t);
        wm.addWorkDay(wd);
        
        wm.getSumPerMonth();
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testCase13() throws Exception {
        Task t = new Task("1234");
        WorkDay wd = new WorkDay(2016, 9, 1);
        WorkMonth wm = new WorkMonth(2016, 9);
        
        wd.addTask(t);
        wm.addWorkDay(wd);
        
        wm.getExtraMinPerMonth();
    }
}
