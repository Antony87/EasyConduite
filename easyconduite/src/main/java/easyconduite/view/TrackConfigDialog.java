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

import easyconduite.controllers.TrackConfigController;
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.UIResourcePlayable;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.view.controls.ActionDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class manage a dialog box, wich exposes configuration UI of a Media.
 *
 * @author antony Fons
 * @since 1.0
 */
public class TrackConfigDialog extends Stage {

    private static final String DIALOG_ERROR_HEADER = "dialog.error.header";
    static final Logger LOG = LogManager.getLogger(TrackConfigDialog.class);

    private static final String PATH_FXML = "/fxml/trackConfig.fxml";

    private TrackConfigController configController;

    public TrackConfigDialog(UIResourcePlayable mediaUI, List<UIResourcePlayable> mediaUIList) {
        super();

        ResourceBundle locale = null;
        final BorderPane dialogPane = new BorderPane();
        this.setTitle("Configuration");
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.DECORATED);
        this.setResizable(false);
        Scene sceneConfig = new Scene(dialogPane);
        this.setScene(sceneConfig);

        try {
            locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML), locale);
            loader.setRoot(dialogPane);
            loader.load();
            configController = loader.getController();
            configController.initConfigData(mediaUI, mediaUIList);

        } catch (IOException | EasyconduiteException e) {
            ActionDialog.showException(locale.getString(DIALOG_ERROR_HEADER), locale.getString("easyconduitecontroler.save.error"),e);
            LOG.error("Error occured during opening Config UI", e);
        }
        this.showAndWait();

    }
}
