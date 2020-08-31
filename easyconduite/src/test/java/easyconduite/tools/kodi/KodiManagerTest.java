/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package easyconduite.tools.kodi;

import easyconduite.exception.RemotePlayableException;
import easyconduite.media.RemoteMedia;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KodiManagerTest {

    private KodiManager manager;

    @BeforeEach
    public void setup() {
        manager=KodiManager.getInstance();
    }

    @Test
    @Order(1)
    void registerOneKodiMedia() throws RemotePlayableException {

        RemoteMedia media1 = new RemoteMedia(RemoteMedia.RemoteType.KODI);
        media1.setName("test1");
        media1.setHost("localhost");
        media1.setPort(8089);
        media1.setResource(Paths.get("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4"));

        manager.registerKodiMedia(media1);
        assertEquals(2, manager.getMapKodiHosts().size());
        manager.registerKodiMedia(media1);
        assertEquals(2, manager.getMapKodiHosts().size());

    }

    @Test
    @Order(2)
    public void registerTwoKodifMedia() throws RemotePlayableException {

        RemoteMedia media1 = new RemoteMedia(RemoteMedia.RemoteType.KODI);
        media1.setName("test1");
        media1.setHost("localhost");
        media1.setPort(8089);
        media1.setResource(Paths.get("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4"));

        manager.registerKodiMedia(media1);

        RemoteMedia media2 = new RemoteMedia(RemoteMedia.RemoteType.KODI);
        media2.setName("test1");
        media2.setHost("localhost");
        media2.setPort(8089);
        // test with test2.mp4 file name.
        media2.setResource(Paths.get("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test2.mp4"));
        manager.registerKodiMedia(media2);
        assertEquals(1, manager.getMapKodiHosts().size());

        RemoteMedia media3 = new RemoteMedia(RemoteMedia.RemoteType.KODI);
        media3.setName("test1");
        media3.setHost("127.0.0.0");
        media3.setPort(8089);
        // test with test2.mp4 file name.
        media3.setResource(Paths.get("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4"));
        manager.registerKodiMedia(media3);

        String hostKey = media3.getHost() + ":" + media3.getPort();
        List<KodiPlayer> kodiPlayerList = manager.getMapKodiHosts().get(hostKey).getKodiMedialist();
        assertEquals(2, manager.getMapKodiHosts().size());
        assertEquals(1, kodiPlayerList.size());


    }
}