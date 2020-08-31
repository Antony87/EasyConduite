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

package easyconduite.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import easyconduite.exception.EasyconduiteException;
import easyconduite.media.MediaFactory;
import easyconduite.media.RemoteMedia;
import easyconduite.model.BaseController;
import easyconduite.model.UIMediaPlayable;
import easyconduite.model.UImediaConfigurable;
import easyconduite.project.ProjectContext;
import easyconduite.view.MediaUIFactory;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class RemoteConfigController extends BaseController implements UImediaConfigurable {

    private static final Logger LOG = LogManager.getLogger(RemoteConfigController.class);

    private static final ProjectContext context = ProjectContext.getContext();

    private UIMediaPlayable mediaUI;

    @FXML
    private JFXTextField hostTextField;
    @FXML
    private JFXTextField portTextField;
    @FXML
    private JFXTextField resourceTextField;

    @FXML
    private VBox trackConfigVbox;

    @FXML
    private CommonConfigController commonConfigController;

    @FXML
    private void handleClickOk(MouseEvent event) {

        if (validateFields()) {
            if (mediaUI == null) {
                final RemoteMedia media = (RemoteMedia) MediaFactory.createPlayableMedia(RemoteMedia.RemoteType.KODI);
                final UIMediaPlayable mediaUI = MediaUIFactory.createMediaUI(media);
                updateMediaValues(media);
                try {
                    media.initPlayer();
                    final MainController controller = context.getMainControler();
                    controller.getMediaUIList().add(mediaUI);
                } catch (EasyconduiteException e) {
                    LOG.error("Error during player init of media {}", media);
                }
            } else {
                final RemoteMedia media = (RemoteMedia) mediaUI.getAbstractMedia();
                updateMediaValues(media);
            }
            mediaUI.actualizeUI();
            close();
        }
    }

    @FXML
    private void handleClickCancel(MouseEvent event) {
        close();
    }

    private void close() {
        final Stage stage = (Stage) trackConfigVbox.getScene().getWindow();
        stage.close();
    }

    private void updateMediaValues(RemoteMedia media) {
        media.setResource(Paths.get(resourceTextField.getText()));
        media.setHost(hostTextField.getText());
        media.setPort(Integer.parseInt(portTextField.getText()));
        commonConfigController.updateCommonsValues(media);
    }

    public boolean validateFields() {
        boolean validate = true;
        if (!portTextField.validate()) validate = false;
        if (!hostTextField.validate()) validate = false;
        if (!resourceTextField.validate()) validate = false;
        return validate;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        commonConfigController.getLoopTrack().setDisable(true);

        RegexValidator portValidator = new RegexValidator();
        portValidator.setRegexPattern("\\d{3,5}");
        portValidator.setMessage(resourceBundle.getString("configuration.port.error"));
        portTextField.getValidators().add(portValidator);
        portTextField.setPromptText("ex : 8089");

        RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
        hostTextField.setPromptText("ex : localhost");
        hostTextField.getValidators().add(requiredValidator);
        resourceTextField.getValidators().add(requiredValidator);
    }

    @Override
    public void updateUI(UIMediaPlayable mediaUI) {
        this.mediaUI = mediaUI;
        final RemoteMedia media = (RemoteMedia) mediaUI.getAbstractMedia();
        resourceTextField.setText(media.getResource().toString());
        hostTextField.setText(media.getHost());
        portTextField.setText(String.valueOf(media.getPort()));
        commonConfigController.updateUI(mediaUI);
    }
}
