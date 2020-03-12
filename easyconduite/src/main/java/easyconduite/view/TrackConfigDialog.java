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
import easyconduite.model.MediaConfigurable;
import easyconduite.model.UIMediaPlayable;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.view.controls.ActionDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
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

    private static final String DIALOG_ERROR_HEADER = "dialog.error.header";
    static final Logger LOG = LogManager.getLogger(TrackConfigDialog.class);

    private static final String PATH_FXML_AUDIO = "/fxml/secondary/AudioConfig_v3.fxml";
    private static final String PATH_FXML_REMOTE = "/fxml/secondary/RemoteConfig_v3.fxml";


    /**
     * Creates a dialog stage with the given MediaUIs.
     *
     * @param mediaUI     The {@link UIMediaPlayable} implementation who will be configured.
     *                    MediaUI controls {@link easyconduite.model.MediaPlayable}.
     * @param mediaUIList
     */
    public TrackConfigDialog(UIMediaPlayable mediaUI, List<UIMediaPlayable> mediaUIList) {
        super();

        ResourceBundle locale = null;
        final VBox trackConfigVbox = new VBox();
        setTitle("Configuration");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.DECORATED);
        setResizable(false);
        Scene sceneConfig = new Scene(trackConfigVbox);
        setScene(sceneConfig);

        String fxmlPath = "";
        if (mediaUI instanceof AudioMediaUI) {
            fxmlPath = PATH_FXML_AUDIO;
        } else if (mediaUI instanceof RemoteMediaUI) {
            fxmlPath = PATH_FXML_REMOTE;
        }
        try {
            locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), locale);
            loader.setRoot(trackConfigVbox);
            loader.load();
            MediaConfigurable configController = loader.getController();
            configController.setFields(mediaUI, mediaUIList);

        } catch (IOException | EasyconduiteException e) {
            ActionDialog.showException(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.save.error"), e);
            LOG.error("Error occured during opening Config UI", e);
        }
        showAndWait();
    }
}
