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
package easyconduite.view;

import easyconduite.exception.EasyconduiteException;
import easyconduite.model.UImediaConfigurable;
import easyconduite.model.UIMediaPlayable;
import easyconduite.util.EasyConduitePropertiesHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * This class creates the dialog in terms of {@link UIMediaPlayable}.<br>
 * If mediaUI is instance of {@link AudioMediaUI} then controller is {@link easyconduite.controllers.AudioConfigController}.<br>
 * If mediaUI is instance of {@link RemoteMediaUI} then controller is {@link easyconduite.controllers.RemoteConfigController}.
 *
 * @author antony Fons
 * @since 1.0
 */
public class TrackConfigDialog extends Stage {

    /**************************************************************************
     * Private fields
     **************************************************************************/

    private ResourceBundle locale;
    private UImediaConfigurable configController;
    private UIMediaPlayable mediaUI;
    private String fxmlPath = "";
    private static final String DIALOG_ERROR_HEADER = "dialog.error.header";
    static final Logger LOG = LogManager.getLogger(TrackConfigDialog.class);

    private static final String PATH_FXML_AUDIO = "/fxml/secondary/AudioConfig_v3.fxml";
    private static final String PATH_FXML_REMOTE = "/fxml/secondary/RemoteConfig_v3.fxml";

    public TrackConfigDialog() {
        super();
    }

    public TrackConfigDialog withRemoteMedia(RemoteMediaUI mediaUI) {
        fxmlPath = PATH_FXML_REMOTE;
        this.mediaUI = mediaUI;
        return this;
    }

    public TrackConfigDialog withAudioMedia(AudioMediaUI mediaUI) {
        this.mediaUI = mediaUI;
        fxmlPath = PATH_FXML_AUDIO;
        return this;
    }

    public TrackConfigDialog withNoMedia() {
        fxmlPath = PATH_FXML_REMOTE;
        return this;
    }


    public void build() throws EasyconduiteException {
        locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        final VBox root = new VBox();
        setTitle("Configuration");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.DECORATED);
        setResizable(false);
        Scene sceneConfig = new Scene(root);
        setScene(sceneConfig);
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), locale);
        loader.setRoot(root);
        try {
            loader.load();
        } catch (IOException e) {
            throw new EasyconduiteException(e.getMessage());
        }
        configController = loader.getController();
        if(mediaUI!=null) configController.updateUI(mediaUI);
        showAndWait();
    }
}
