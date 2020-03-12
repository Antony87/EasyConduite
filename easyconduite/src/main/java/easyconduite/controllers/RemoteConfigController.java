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

import easyconduite.exception.EasyconduiteException;
import easyconduite.media.RemoteMedia;
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
import java.util.List;
import java.util.Set;

public class RemoteConfigController extends BaseController implements MediaConfigurable {

    static final Logger LOG = LogManager.getLogger(RemoteConfigController.class);

    private UIMediaPlayable mediaUI;

    @FXML
    private TextField hostTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField resourceTextField;

    @FXML
    private GridPane commonConfig;

    @FXML
    private VBox trackConfigVbox;

    @FXML
    private CommonConfigController commonConfigController;

    @FXML
    private void handleClickOk(MouseEvent event) {

        if(validateFields()){
            final RemoteMedia media = (RemoteMedia) mediaUI.getAbstractMedia();
            if (!media.isInitialized()){
                try {
                    saveMediaProperties(media);
                    media.initPlayer();
                } catch (EasyconduiteException e) {
                    e.printStackTrace();
                }
            }else{
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

    private void saveMediaProperties(RemoteMedia media){
        final URI resourceURI = new File(getResourceTextField()).toURI();
        media.setResource(resourceURI);
        media.setHost(getHostTextField());
        media.setPort(Integer.parseInt(getPortTextField()));
        commonConfigController.saveCommonsProperties(media);
    }


    public boolean validateFields() {

        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        final Set<ConstraintViolation<RemoteConfigController>> violation = validator.validate(this);
        if (!violation.isEmpty()) {
            violation.forEach(r -> {
                LOG.trace("Property Path {}", r.getPropertyPath());
                String textFieldName=r.getPropertyPath().toString();
                ((TextField)trackConfigVbox.lookup("#"+textFieldName)).setPromptText("enter a value please !");
            });
            return false;
        }

        return true;
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
