/*
 * Copyright (C) 2016 Antony Fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.util;

import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import easyconduite.exception.PersistenceException;
import easyconduite.objects.EasyconduiteProperty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author antony
 */
public class PersistenceUtilTest {

    private File tempFile;

    static final Logger LOG = LogManager.getLogger(PersistenceUtilTest.class);

    @Before
    public void setUp() {
        try {
            Path tempPath = Files.createTempFile("testEasyConduite", ".ecp");
            tempFile = tempPath.toFile();
            LOG.trace("create {} file within setUp", tempFile.getName());
        } catch (IOException ex) {
            LOG.error("Error create temp file testEasyConduite.ecp", ex);
        }
    }

    @After
    public void tearDown() {
        if (Files.exists(tempFile.toPath())) {
            if (tempFile.delete()) {
                LOG.trace("delete file within tearDown");
            }
        }
    }

    @Test
    public void testSaveAudioTable() throws Exception {
        AudioTable audioTable = new AudioTable();
        audioTable.setName("testEasyConduite");
        //PersistenceUtil.saveAudioTable(tempFile, audioTable);
        PersistenceUtil.writeToFile(tempFile, audioTable, PersistenceUtil.FILE_TYPE.XML);

        assertTrue(Files.exists(tempFile.toPath()));
        assertTrue(Files.size(tempFile.toPath()) > 5L);

    }

    @Test
    public void testOpenAudioTable() throws PersistenceException {
        // saveAudioTable before openAudioTable
        final AudioMedia mediaExpected = new AudioMedia(tempFile);
        mediaExpected.repeatableProperty().setValue(Boolean.TRUE);
        mediaExpected.nameProperty().setValue("mediatest");

        final AudioTable tableExpected = new AudioTable();
        tableExpected.getAudioMediaList().add(mediaExpected);
        tableExpected.setName("testEasyConduite");
        //PersistenceUtil.saveAudioTable(tempFile, tableExpected);
        PersistenceUtil.writeToFile(tempFile, tableExpected, PersistenceUtil.FILE_TYPE.XML);

        final AudioTable audiotable = PersistenceUtil.readFromFile(tempFile, AudioTable.class, PersistenceUtil.FILE_TYPE.XML);
        assertEquals(tableExpected.getName(), audiotable.getName());
        final AudioMedia media = audiotable.getAudioMediaList().get(0);
        assertNotNull(media);
        assertEquals(mediaExpected.getName(), media.getName());
    }

    @Test
    public void testSaveUserData() throws PersistenceException {
        final EasyconduiteProperty userData = new EasyconduiteProperty();
        userData.setLocale(Locale.FRENCH);
        userData.setWindowWith(300);
        //PersistenceUtil.saveUserData(tempFile, userData);
        PersistenceUtil.writeToFile(tempFile, userData, PersistenceUtil.FILE_TYPE.BIN);
        assertTrue(Files.exists(tempFile.toPath()));
    }

    @Test
    public void testOpenUserData() throws PersistenceException {
        final EasyconduiteProperty userData = new EasyconduiteProperty();
        userData.setLocale(Locale.FRENCH);
        userData.setWindowWith(300);
        //PersistenceUtil.saveUserData(tempFile, userData);
        PersistenceUtil.writeToFile(tempFile, userData, PersistenceUtil.FILE_TYPE.BIN);

        final EasyconduiteProperty userDataread = PersistenceUtil.readFromFile(tempFile, EasyconduiteProperty.class, PersistenceUtil.FILE_TYPE.BIN);

        assertNotNull(userDataread);
        assertEquals(userData.getWindowWith(), userDataread.getWindowWith());

    }
}
