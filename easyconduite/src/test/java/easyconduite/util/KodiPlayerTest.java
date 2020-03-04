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

package easyconduite.util;

import easyconduite.exception.RemotePlayableException;
import easyconduite.objects.media.MediaStatus;
import easyconduite.objects.media.RemoteMedia;
import easyconduite.tools.kodi.KodiPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KodiPlayerTest {

    private RemoteMedia remote;

    @BeforeAll
    public void setup() {
        remote = new RemoteMedia(RemoteMedia.Type.KODI);
        remote.setResource(new File("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4").toURI());
        remote.setPort(8089);
        remote.setHost("localhost");
    }

    @Test
    void isKodiActive() {

        KodiPlayer handler = null;
        try {
            handler = new KodiPlayer(remote);
        } catch (RemotePlayableException e) {
            e.printStackTrace();
        }
        assertEquals(true, handler.isActive());
    }

    @Test
    void getActivePlayer() {

        KodiPlayer handler = null;
        try {
            handler = new KodiPlayer(remote);
            long startTime = System.currentTimeMillis();
            Integer id = handler.getActivePlayerId();
            long endTime = System.currentTimeMillis();
            System.out.println(endTime-startTime);
        } catch (RemotePlayableException e) {
            e.printStackTrace();
        }
    }

    @Test
    void playingTest() {
        KodiPlayer handler = null;
        try {
            handler = new KodiPlayer(remote);
        } catch (RemotePlayableException e) {
            e.printStackTrace();
        }
        final MediaStatus mediaStatus = handler.play();
        assertEquals(MediaStatus.PLAYING, mediaStatus);
    }
}