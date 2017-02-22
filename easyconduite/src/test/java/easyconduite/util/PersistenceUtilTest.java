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

import easyconduite.objects.AudioTable;
import easyconduite.objects.PersistenceException;
import java.io.File;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author antony
 */
public class PersistenceUtilTest {
    
    public PersistenceUtilTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    @Ignore
    public void testSave() throws Exception {
        System.out.println("save");
        AudioTable audioTable = new AudioTable();
        audioTable.setName("test");
        PersistenceUtil.save(new File("saveTest.ecp"), audioTable);
        
        File saved = new File("saveTest.ecp");
        assertTrue(saved.exists());
    }
    
    @Test
    @Ignore
    public void testOpen() throws PersistenceException {
        System.out.println("open");
        AudioTable audiotable = null;
        audiotable = PersistenceUtil.open(new File("saveTest.ecp"));
        assertEquals("test", audiotable.getName());
        
    }
    
    @Test
    public void testCurrentRepertory(){
        
        System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
    }
}
