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

import easyconduite.model.EasyAudioChain;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioMediaConfigurator;
import easyconduite.ui.commons.ActionDialog;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is the controller for TrackConfigDialog.
 *
 * @author antony
 */
public class TrackConfigController extends BorderPane implements Initializable, EasyAudioChain {

    static final Logger LOG = LogManager.getLogger(TrackConfigController.class);

    private EasyconduiteController mainController;

    private AudioMedia audioMedia;

    private KeyCode newKeyCode;

    @FXML
    private BorderPane configDialogPane;

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

    private final AudioMediaConfigurator mediaConfigurator;

    private EasyAudioChain nextChain;

    public TrackConfigController() {
        mediaConfigurator = new AudioMediaConfigurator();
    }

    /**
     * Initializes controller after DialogPane loaded.<br>
     * Juts setting up the spinners.
     *
     * @param location
     * @param resources
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.trace("TrackConfigController initialized");
        SpinnerValueFactory<Integer> valueFadeInFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeInSpinner.setValueFactory(valueFadeInFactory);
        SpinnerValueFactory<Integer> valueFadeOutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeOutSpinner.setValueFactory(valueFadeOutFactory);
    }

    @FXML
    private void handleClickOk(MouseEvent event) {

        Integer iValueFadeOut = (Integer) fadeOutSpinner.getValue();
        Integer iValueFadeIn = (Integer) fadeInSpinner.getValue();
        mediaConfigurator
                .withName(nametrackfield.getText())
                .withfadeIn(Duration.seconds(iValueFadeIn))
                .withfadeOut(Duration.seconds(iValueFadeOut));
        this.updateFromAudioMedia(this.audioMedia);
        this.close();
    }

    @FXML
    private void handleClickCancel(MouseEvent event) {
        this.close();
    }

    @FXML
    private void handleClickRepeat(MouseEvent event) {
        mediaConfigurator.withRepeat(repeattrack.selectedProperty().getValue());
    }

    @FXML
    private void handleClickKeyField(MouseEvent event) {
        mediaConfigurator.withKeyCodeChanged(null);
        keytrackfield.clear();
    }

    @FXML
    private void handleKeyReleasedTrack(KeyEvent event) {
        final KeyCode typedKeycode = event.getCode();
        if (mainController.isKeyCodeExist(typedKeycode)) {
            keytrackfield.clear();
            ActionDialog.showWarning(String.format("La touche %s est déja attribuée", KeyCodeUtil.toString(typedKeycode)), "Cliquez sur OK pour recommencer");
        } else {
            if (typedKeycode != this.audioMedia.getKeycode()) {
                this.newKeyCode = typedKeycode;
                keytrackfield.setText(KeyCodeUtil.toString(typedKeycode));
                mediaConfigurator.withKeyCodeChanged(newKeyCode);
            }
        }
    }

    /**
     * This method sets up AudioMedia, MainController and fields for this
     * controller.
     *
     * @param media
     * @param controller
     */
    public void setup(AudioMedia media, EasyconduiteController controller) {
        this.audioMedia = media;
        if (this.audioMedia.getFadeInDuration() != null) {
            fadeInSpinner.getValueFactory().setValue((int) this.audioMedia.getFadeInDuration().toSeconds());
        }
        if (this.audioMedia.getFadeOutDuration() != null) {
            fadeOutSpinner.getValueFactory().setValue((int) this.audioMedia.getFadeOutDuration().toSeconds());
        }
        mainController = controller;
        nametrackfield.setText(media.getName());
        keytrackfield.setText(KeyCodeUtil.toString(media.getKeycode()));
        repeattrack.setSelected(media.getRepeatable());
    }

    private void close() {
        final Stage stage = (Stage) configDialogPane.getScene().getWindow();
        stage.close();
    }

    @Override
    public void setNext(EasyAudioChain next) {
        this.nextChain = next;
    }

    @Override
    public void updateFromAudioMedia(AudioMedia media) {
        mediaConfigurator.update(this.audioMedia);
        setNext(mainController);
        LOG.trace("After config, AudioMedia is {}", this.audioMedia);
        nextChain.updateFromAudioMedia(this.audioMedia);
    }

    @Override
    public void removeChilds(AudioMedia audioMedia) {
    }
}
