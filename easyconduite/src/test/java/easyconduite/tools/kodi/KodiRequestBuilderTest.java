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

import easyconduite.objects.media.RemotePlayer;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class KodiRequestBuilderTest {

    @Test
    void build() {
        RemotePlayer remote = new RemotePlayer(RemotePlayer.Type.KODI);
        remote.setResource(new File("C:/Users/V902832/IdeaProjects/EasyConduite/easyconduite/src/test/resources/test.mp4").toURI());
        final File file = new File(remote.getResource());

        System.out.println(new KodiRequestBuilder(KodiMethods.OPEN).setFile(file).build());



    }
}