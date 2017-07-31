package com.kristofdan.tlog16rs.core.beans;;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.kristofdan.tlog16rs.core.exceptions.NotNewMonthException;


public class TimeLoggerTest {

    public TimeLoggerTest() {
    }

    @Test
    public void testCase1() throws Exception {
        Task t = new Task("1234","","07:30","10:30");
        WorkDay wd = new WorkDay(2016, 4, 14);
        WorkMonth wm = new WorkMonth(2016, 4);
        TimeLogger tl = new TimeLogger("");
        
        wd.addTask(t);
        wm.addWorkDay(wd);
        tl.addMonth(wm);
        
        WorkMonth firstMonthInLogger = tl.getMonths().get(0);
        assertEquals(t.getMinPerTask(), firstMonthInLogger.getSumPerMonth());
    }
    
    @Test(expected = NotNewMonthException.class)
    public void testCase2() throws Exception {
        WorkMonth wm1 = new WorkMonth(2016, 4);
        WorkMonth wm2 = new WorkMonth(2016, 4);
        TimeLogger tl = new TimeLogger("");
        
        tl.addMonth(wm1);
        tl.addMonth(wm2);
    }
    
}
