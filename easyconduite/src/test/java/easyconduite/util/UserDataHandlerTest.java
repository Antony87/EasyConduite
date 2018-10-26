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

import easyconduite.objects.ApplicationProperties;
import easyconduite.tools.ApplicationPropertiesHelper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author antony
 */
public class UserDataHandlerTest {

    private static final Path FILE_PATH = Paths.get("userdata.dat");

    static final Logger LOG = LogManager.getLogger(UserDataHandlerTest.class);

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        if (Files.exists(FILE_PATH)) {
            if (FILE_PATH.toFile().delete()) {
                LOG.trace("delete file within tearDown");
            }
        }
    }

    /**
     * Test of getProperties method, of class ApplicationPropertiesHelper.
     */
    @Test
    @Ignore
    public void testGetUserData() {
        ApplicationPropertiesHelper handler = ApplicationPropertiesHelper.getInstance();
        ApplicationProperties userdata = handler.getProperties();
        assertNotNull(userdata.getLocale());
        LOG.debug("Locale {}",userdata.getLocale());
    }

    /**
     * Test of getInstance method, of class ApplicationPropertiesHelper.
     */
    @Test
    public void testGetInstance() {
        assertNotNull(ApplicationPropertiesHelper.getInstance());
    }

}
