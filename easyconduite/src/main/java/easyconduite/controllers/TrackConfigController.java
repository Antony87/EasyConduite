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
package easyconduite.controllers;

import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioMediaConfigurator;
import easyconduite.ui.ActionDialog;
import easyconduite.ui.LinkKeyBoardDialog;
import easyconduite.util.KeyCodeUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author antony
 */
public class TrackConfigController implements Initializable {

    static final Logger LOG = LogManager.getLogger(TrackConfigController.class);

    private EasyconduiteController mainController;

    private AudioMedia audioMedia;

    private LinkKeyBoardDialog configDialog;

    private KeyCode newKeyCode;

    @FXML
    private TextField nametrackfield;

    @FXML
    private TextField keytrackfield;

    @FXML
    private CheckBox repeattrack;

    @FXML
    private Spinner fadeInSpinner;

    @FXML
    private Spinner fadeOutSpinner;

    @FXML
    private Button cancelbutton;

    @FXML
    private Button okbutton;

    private AudioMediaConfigurator mediaConfigurator;

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.trace("TrackConfigController initialized");
    }

    @FXML
    public void handleClickOk(MouseEvent event) {
        mediaConfigurator = mediaConfigurator.withName(nametrackfield.getText());
        mainController.updateAudioMedia(mediaConfigurator, audioMedia);
        configDialog.close();
    }

    @FXML
    public void handleClickRepeat(MouseEvent event) {
        mediaConfigurator = mediaConfigurator.withRepeat(repeattrack.selectedProperty().getValue());
    }

    @FXML
    public void handleClickCancel(MouseEvent event) {
        mediaConfigurator = null;
        configDialog.close();
    }

    @FXML
    public void handleClickKeyField(MouseEvent event) {
        mediaConfigurator = mediaConfigurator.withKeyCodeChanged(null);
        keytrackfield.clear();
    }

    @FXML
    public void handleKeyReleasedTrack(KeyEvent event) {
        final KeyCode typedKeycode = event.getCode();
        if (mainController.isKeyCodeExist(typedKeycode)) {
            keytrackfield.clear();
            ActionDialog.showWarning(String.format("La touche %s est déja affectée", KeyCodeUtil.toString(typedKeycode)), "Cliquez sur OK pour recommencer");
        } else {
            if (typedKeycode != audioMedia.getKeycode()) {
                setNewKeyCode(typedKeycode);
                keytrackfield.setText(KeyCodeUtil.toString(typedKeycode));
                mediaConfigurator = mediaConfigurator.withKeyCodeChanged(newKeyCode);
            }
        }

    }

    public void setAudioConfig(AudioMedia media, EasyconduiteController controller, LinkKeyBoardDialog configDialog) {
        this.mainController = controller;
        audioMedia = media;
        nametrackfield.setText(audioMedia.getName());
        keytrackfield.setText(KeyCodeUtil.toString(audioMedia.getKeycode()));
        repeattrack.setSelected(audioMedia.getRepeatable());
        this.configDialog = configDialog;

        SpinnerValueFactory<Integer> valueFadeInFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeInSpinner.setValueFactory(valueFadeInFactory);
        SpinnerValueFactory<Integer> valueFadeOutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeOutSpinner.setValueFactory(valueFadeOutFactory);

        mediaConfigurator = new AudioMediaConfigurator();

    }

    public void setNewKeyCode(KeyCode newKeyCode) {
        this.newKeyCode = newKeyCode;
    }

}
