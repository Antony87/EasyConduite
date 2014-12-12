/*
 * Copyright (C) 2014 V902832
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
package easyconduite.application;

import easyconduite.util.PersistenceUtil;
import easyconduite.objects.AudioMedia;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.KeyCode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author V902832
 */
public class PersistenceUtilTest {
    
    private File unFile;
    
    private static List<AudioMedia> audioMedias;
    
    public PersistenceUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        audioMedias = new ArrayList<>();
        
        AudioMedia unMedia = new AudioMedia(new File("toto"));
        unMedia.setLinkedKeyCode(KeyCode.BACK_SPACE);
        unMedia.setName("Table Toto");
        unMedia.setVolume(0.5);
        audioMedias.add(unMedia);
        
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of save method, of class PersistenceUtil.
     */
//    @Test
//    public void testSave() throws IOException {
//        System.out.println("save");
//        File file = null;
//        PersistenceUtil instance = new PersistenceUtil();
//        instance.save(file, audioMedias);
//        System.out.println(instance.getXml());
//        fail("The test case is a prototype.");
//    }

}
