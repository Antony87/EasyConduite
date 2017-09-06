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

import easyconduite.objects.EasyconduiteProperty;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author antony
 */
public class UserDataHandlerTest {

    private static final Path FILE_PATH = Paths.get("user.dat");

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
     * Test of getProperties method, of class EasyConduitePropertiesHandler.
     */
    @Test
    public void testGetUserData() {
        EasyConduitePropertiesHandler handler = EasyConduitePropertiesHandler.getInstance();
        EasyconduiteProperty userdata = handler.getProperties();
        assertNotNull(userdata.getLocale());
        LOG.debug("Locale {}",userdata.getLocale());
    }

    /**
     * Test of getInstance method, of class EasyConduitePropertiesHandler.
     */
    @Test
    public void testGetInstance() {

        EasyConduitePropertiesHandler handler = EasyConduitePropertiesHandler.getInstance();
        assertNotNull(handler);

    }

}
