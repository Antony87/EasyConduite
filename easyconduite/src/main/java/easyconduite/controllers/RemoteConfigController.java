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
import easyconduite.media.RemoteMedia;
import easyconduite.model.BaseController;
import easyconduite.model.MediaConfigurable;
import easyconduite.model.UIMediaPlayable;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RemoteConfigController extends BaseController implements MediaConfigurable {

    private static final Logger LOG = LogManager.getLogger(RemoteConfigController.class);

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
            final RemoteMedia media = (RemoteMedia) mediaUI.getAbstractMedia();
            if (!media.isInitialized()) {
                try {
                    saveMediaProperties(media);
                    media.initPlayer();
                } catch (EasyconduiteException e) {
                    LOG.error("Error during player init of media {}", media);
                }
            } else {
                saveMediaProperties(media);
            }
            mediaUI.actualizeUI();
            LOG.debug("Media changed {}", media);
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

    private void saveMediaProperties(RemoteMedia media) {
        final URI resourceURI = new File(resourceTextField.getText()).toURI();
        media.setResource(resourceURI);
        media.setHost(hostTextField.getText());
        media.setPort(Integer.parseInt(portTextField.getText()));
        commonConfigController.saveCommonsProperties(media);
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
    public void setFields(UIMediaPlayable mediaUI, List<UIMediaPlayable> mediaUIs) {
        this.mediaUI = mediaUI;
        commonConfigController.setFields(mediaUI, mediaUIs);
    }
}
