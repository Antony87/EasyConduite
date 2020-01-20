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

package easyconduite.view.controls;

import easyconduite.exception.EasyconduiteException;
import easyconduite.objects.project.MediaProject;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;


public class FileChooserControlTest extends ApplicationTest {

    MediaProject projet;
    Scene scene;FileChooser fileChooser;


    @Start
    public void start(Stage stage) throws EasyconduiteException {
        projet = new MediaProject();
        projet.setName("test");
        scene = new Scene(new StackPane(), 800, 600);
        stage.setScene(scene);
        stage.show();
        fileChooser = new FileChooserControl.FileChooserBuilder().asType(FileChooserControl.Action.OPEN_AUDIO).build();
        fileChooser.showOpenDialog(null);
    }

    @Test
    public void testOpenFile() throws EasyconduiteException {

    }
}