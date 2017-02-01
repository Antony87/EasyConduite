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
import java.net.URISyntaxException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author antony
 */
public class AudioMediaConfiguratorTest {

    private File file;

    @Before
    public void setUp() throws URISyntaxException {
        file = new File(this.getClass().getResource("../../testfile.ecp").toURI());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void updaterTest() {
//        AudioMedia media = new AudioMedia(file);
//        Assert.assertEquals(50d, media.getVolume(), 0);
//        AudioMediaConfigurator updater = new AudioMediaConfigurator().withVolume(70d);
//        updater.update(media);
//        Assert.assertEquals(70d, media.getVolume(), 0);
//        Assert.assertNull(media.getKeycode());
//        //updater = new AudioMediaConfigurator.Builder().withRepeat(Boolean.TRUE).build();
//        updater = updater.withRepeat(Boolean.TRUE);
//        updater.update(media);
//        Assert.assertEquals(70d, media.getVolume(), 0);
//        Assert.assertTrue(media.getRepeatable());
//        Assert.assertNull(media.getKeycode());

    }

}
