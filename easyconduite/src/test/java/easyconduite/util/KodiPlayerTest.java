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
import easyconduite.model.AbstractMedia;
import easyconduite.objects.media.RemotePlayer;
import easyconduite.tools.kodi.KodiPlayer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class KodiPlayerTest {

    @Test
    void isKodiActive() {
        RemotePlayer remote = new RemotePlayer(RemotePlayer.Type.KODI);
        remote.setResource(new File("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4").toURI());
        remote.setPort(8089);
        remote.setHost("localhost");
        KodiPlayer handler = null;
        try {
            handler = new KodiPlayer(remote);
        } catch (RemotePlayableException e) {
            e.printStackTrace();
        }
        assertEquals(true,handler.isActive());
    }

    @Test
    void getActivePlayer() {
        RemotePlayer remote = new RemotePlayer(RemotePlayer.Type.KODI);
        remote.setResource(new File("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4").toURI());
        remote.setPort(8089);
        remote.setHost("localhost");
        KodiPlayer handler = null;
        try {
            handler = new KodiPlayer(remote);
        } catch (RemotePlayableException e) {
            e.printStackTrace();
        }
    }

    @Test
    void playingTest(){
        RemotePlayer remote = new RemotePlayer(RemotePlayer.Type.KODI);
        remote.setResource(new File("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4").toURI());
        remote.setPort(8089);
        remote.setHost("localhost");
        KodiPlayer handler = null;
        try {
            handler = new KodiPlayer(remote);
        } catch (RemotePlayableException e) {
            e.printStackTrace();
        }
        final AbstractMedia.MediaStatus mediaStatus = handler.play();
        assertEquals(AbstractMedia.MediaStatus.PLAYING, mediaStatus);
    }
}