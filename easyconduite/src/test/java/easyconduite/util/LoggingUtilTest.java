/*
 * Copyright (C) 2017 Antony Fons
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package easyconduite.util;

import java.util.Collection;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author antony
 */
public class LoggingUtilTest {

    private LoggerContext ctx;
    private Configuration config;

    private static final Logger LOG = LogManager.getLogger(LoggingUtilTest.class);

    @Before
    public void setUp() {
        ctx = (LoggerContext) LogManager.getContext(false);
        
        config = ctx.getConfiguration();
       
        Map<String,LoggerConfig> loggers = config.getLoggers();
        for (Map.Entry<String, LoggerConfig> entry : loggers.entrySet()) {
            String key = entry.getKey();
            LoggerConfig value = entry.getValue();
            System.out.println(key);
            System.out.println(value.getLevel());
            Appender appender = config.getAppender("easyconduiteLogFile");
            value.addAppender(appender, Level.FATAL,null);   
        }     
    }

    @After
    public void tearDown() {
        LoggingUtil.setLog4jLevel(Level.TRACE);
    }

    /**
     * Test of setLog4jLevel method, of class LoggingUtil.
     */
    @Test
    public void testSetLog4jLevel() {
        assertEquals(Level.TRACE, LOG.getLevel());
        LoggingUtil.setLog4jLevel(Level.FATAL);
        LOG.fatal("LoggingUtil.setLog4jLevel to FATAL");
        assertEquals(Level.FATAL, LOG.getLevel());
    }

}
