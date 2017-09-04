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
package easyconduite.objects;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Objects;
import javax.xml.bind.JAXB;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author antony
 */
public class AudioTableTest {

    private AudioTable table;

    public AudioTableTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void JabxTest() throws URISyntaxException {

        File file = new File(this.getClass().getResource("../../testfile.ecp").toURI());
        //AudioMedia media = new AudioMedia(file);
        AudioTable table = new AudioTable();
        table.getAudioMediaList().add(new AudioMedia(file));
        table.getAudioMediaList().add(new AudioMedia(file));

        StringWriter writer = new StringWriter();
        JAXB.marshal(table, writer);

        String xmlString = writer.toString();
        System.out.println(xmlString);
        
        AudioTable result = JAXB.unmarshal(new StringReader(xmlString), AudioTable.class);
        Assert.assertTrue(result.getAudioMediaList().size()==2);
        Assert.assertFalse(Objects.equals(table.getAudioMediaList().get(0), table.getAudioMediaList().get(1)));
        System.out.println(table.getAudioMediaList().get(0).getUniqueId());
        System.out.println(table.getAudioMediaList().get(1).getUniqueId());

    }

}
