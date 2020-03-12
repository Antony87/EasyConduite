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

import easyconduite.media.RemoteMedia;
import easyconduite.model.AbstractMedia;
import easyconduite.model.BaseController;
import easyconduite.model.MediaConfigurable;
import easyconduite.model.UIMediaPlayable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class RemoteConfigController extends BaseController implements MediaConfigurable {

    static final Logger LOG = LogManager.getLogger(RemoteConfigController.class);
    private Validator validator;

    private UIMediaPlayable mediaUI;

    private RemoteMedia remoteMedia;
    @FXML
    private TextField hostTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField resourceTextField;
    @FXML
    private GridPane specificPane;

    @FXML
    private GridPane commonConfig;

    @FXML
    private VBox trackConfigVbox;

    @FXML
    private CommonConfigController commonConfigController;

    @FXML
    private void handleClickOk(MouseEvent event) {

        final RemoteMedia media = (RemoteMedia) mediaUI.getAbstractMedia();
        commonConfigController.saveCommonsProperties(media);
        mediaUI.actualizeUI();
        LOG.debug("Media changed {}", media);
        this.close();
    }

    @FXML
    private void handleClickCancel(MouseEvent event) {
        this.close();
    }

    private void close() {
        final Stage stage = (Stage) trackConfigVbox.getScene().getWindow();
        stage.close();
    }


    public boolean updateSpecificMedia(AbstractMedia media) {

        final Set<ConstraintViolation<RemoteConfigController>> violation = validator.validate(this);
        if (violation.size() > 0) {
            violation.forEach(r -> {
                LOG.trace("Property Path {}", r.getPropertyPath().toString());
                String textFieldName=r.getPropertyPath().toString();
                ((TextField)specificPane.lookup("#"+textFieldName)).setPromptText("enter a value please !");
            });
            return false;
        }
        remoteMedia = (RemoteMedia) media;
        final URI resourceURI = new File(resourceTextField.getText()).toURI();
        remoteMedia.setResource(resourceURI);
        remoteMedia.setHost(hostTextField.getText());
        remoteMedia.setPort(Integer.valueOf(portTextField.getText()));
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @NotBlank
    public String getHostTextField() {
        return hostTextField.getText();
    }

    @NotBlank
    @Pattern(regexp = "\\d{3,5}")
    public String getPortTextField() {
        return portTextField.getText();
    }

    @NotBlank
    public String getResourceTextField() {
        return resourceTextField.getText();
    }

    @Override
    public void setFields(UIMediaPlayable mediaUI, List<UIMediaPlayable> mediaUIs) {
        this.mediaUI = mediaUI;
        commonConfigController.setFields(mediaUI, mediaUIs);
    }
}
