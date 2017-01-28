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
package easyconduite.ui;

import easyconduite.controllers.EasyconduiteController;
import easyconduite.controllers.TrackConfigController;
import easyconduite.objects.AudioMedia;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class manage a dialog box, wich exposes affected key, name and repeat
 * for an audio track.
 *
 * @author antony Fons
 */
public class LinkKeyBoardDialog extends Stage {

    static final Logger LOG = LogManager.getLogger(LinkKeyBoardDialog.class);

    private static final String PATH_FXML = "/fxml/trackDialog.fxml";

    public LinkKeyBoardDialog(AudioMedia media, EasyconduiteController mainController) throws IOException {
        
        LOG.debug("LinkKeyBoardDialog with AudioMedia[{}] and EasyconduiteController[{}]", media.getFilePathName(), mainController);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML));

        // Initialize controllers        
        BorderPane dialogPane = (BorderPane) loader.load();
        TrackConfigController configController = loader.getController();

        this.setTitle("Configuration");
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);

        this.setScene(new Scene(dialogPane));
        
        configController.setAudioConfig(media,mainController,LinkKeyBoardDialog.this);
        
        this.show();
    }
}
