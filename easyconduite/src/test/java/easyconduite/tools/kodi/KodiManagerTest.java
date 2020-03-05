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
import easyconduite.tools.HttpClientForMedias;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KodiManagerTest {

    KodiManager observer;
    RemoteMedia remote1;
    RemoteMedia remote2;
    RemoteMedia remote3;

    @BeforeAll
    public void setup() {

        observer = KodiManager.getInstance();
        remote1 = new RemoteMedia(RemoteMedia.Type.KODI);
        remote1.setResource(new File("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4").toURI());
        remote1.setPort(8089);
        remote1.setHost("localhost");
        remote1.setName("media1");

        remote2 = new RemoteMedia(RemoteMedia.Type.KODI);
        remote2.setResource(new File("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4").toURI());
        remote2.setPort(8089);
        remote2.setHost("localhost");
        remote2.setName("media2");

        remote3 = new RemoteMedia(RemoteMedia.Type.KODI);
        remote3.setResource(new File("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4").toURI());
        remote3.setPort(8089);
        remote3.setHost("127.0.0.0");
        remote3.setName("media3");


    }

    @Test
    @Order(1)
    void registerKodiPlayer() throws URISyntaxException {

        observer.registerKodiMedia(remote1);
        observer.registerKodiMedia(remote2);
        HttpClientForMedias httpClientForMedias = observer.getMediaHostMap().get(remote1.getHost());
        assertEquals(1, observer.getMediaHostMap().size());
        assertEquals(2, httpClientForMedias.getMediaList().size());

    }

    @Test
    @Order(2)
    void alreadyRegistrered() {

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                observer.registerKodiMedia(remote2);
            }
        });

    }

    @Test
    @Order(3)
    void unRegisterException() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                observer.unRegisterKodiMedia(remote3);
            }
        });
    }

    @Test
    @Order(4)
    void registredOtherHost() throws URISyntaxException {
        observer.registerKodiMedia(remote3);
        assertEquals(2, observer.getMediaHostMap().size());
    }

    @Test
    @Order(5)
    void getActivePlayer() {
        long startTime = System.currentTimeMillis();
        Integer activePlayer = observer.getActivePlayer(remote1.getHost());
        long endTime = System.currentTimeMillis();
        System.out.println(activePlayer + " " + (endTime - startTime));
    }

    @Test
    @Order(6)
    void getItem() throws RemotePlayableException {

        Integer activePlayer = observer.getActivePlayer(remote1.getHost());
        if(activePlayer!=null){
            long startTime = System.currentTimeMillis();
            KodiItemResponse.KodiItem item = observer.getItem(remote1.getHost(),activePlayer);
            System.out.println(item.getFile());
            long endTime = System.currentTimeMillis();
            System.out.println(activePlayer + " " + (endTime - startTime));
        }
    }

    @Test
    void getItemWithNull(){
        assertThrows(RemotePlayableException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                final Integer activePlayer=null;
                KodiItemResponse.KodiItem item = observer.getItem(remote1.getHost(),activePlayer);
            }
        });
    }
}