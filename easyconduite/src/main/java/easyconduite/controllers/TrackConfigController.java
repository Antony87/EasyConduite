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

import easyconduite.model.DialogAbstractController;
import easyconduite.model.EasyMedia;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.util.KeyCodeHelper;
import easyconduite.view.AudioMediaUI;
import easyconduite.view.controls.ActionDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller for TrackConfigDialog.
 *
 * @author antony
 */
public class TrackConfigController extends DialogAbstractController implements Initializable {

    static final Logger LOG = LogManager.getLogger(TrackConfigController.class);
    private static final String KEY_ASSIGN_ERROR = "trackconfigcontroller.key.error";
    private static final String KEY_ASSIGN_OTHER = "trackconfigcontroller.key.other";

    private MainController mainController;

    private KeyCode newKeyCode;

    @FXML
    private BorderPane trackConfigPane;

    @FXML
    private TextField nametrackfield;

    @FXML
    private TextField keytrackfield;

    @FXML
    private CheckBox loopTrack;

    @FXML
    private Spinner fadeInSpinner;

    @FXML
    private Spinner fadeOutSpinner;

    private AudioMediaUI mediaUI;

    public TrackConfigController() {
        super();
    }

    /**
     * Initializes controller after DialogPane loaded.<br>
     * Juts setting up the spinners.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.trace("TrackConfigController initialized");
        final EasyMedia media = this.mediaUI.getEasyMedia();
        nametrackfield.setText(media.getName());
        keytrackfield.setText(KeyCodeHelper.toString(media.getKeycode()));
        loopTrack.setSelected(media.getLoppable());
        initializeSpinners(fadeInSpinner, fadeOutSpinner, (AudioVideoMedia) media);

    }

    @FXML
    private void handleClickOk(MouseEvent event) {

        Integer iValueFadeOut = (Integer) fadeOutSpinner.getValue();
        Integer iValueFadeIn = (Integer) fadeInSpinner.getValue();

        this.close(trackConfigPane);
    }

    @FXML
    private void handleClickCancel(MouseEvent event) {
        this.close(trackConfigPane);
    }

    @FXML
    private void handleClickRepeat(MouseEvent event) {

    }

    @FXML
    private void handleClickKeyField(MouseEvent event) {

        keytrackfield.clear();
    }

    @FXML
    private void handleKeyReleasedTrack(KeyEvent event) {
        final KeyCode typedKeycode = event.getCode();
        if (mainController.isKeyCodeExist(typedKeycode)) {
            keytrackfield.clear();
            final ResourceBundle bundle = ApplicationPropertiesHelper.getInstance().getLocalBundle();
            ActionDialog.showWarning(String.format(bundle.getString(KEY_ASSIGN_ERROR), KeyCodeHelper.toString(typedKeycode)), bundle.getString(KEY_ASSIGN_OTHER));
        } else {
            final AudioVideoMedia audioMedia = (AudioVideoMedia) this.mediaUI.getEasyMedia();
            if (typedKeycode != audioMedia.getKeycode()) {
                this.newKeyCode = typedKeycode;
                keytrackfield.setText(KeyCodeHelper.toString(typedKeycode));
                mainController.updateKeyCodeList();
            }
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setMediaUI(AudioMediaUI mediaUI) {
        this.mediaUI = mediaUI;
    }

    private void initializeSpinners(Spinner<Integer> fadeIn, Spinner<Integer> fadeOut, AudioVideoMedia audioMedia) {

        SpinnerValueFactory<Integer> valueFadeInFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeIn.setValueFactory(valueFadeInFactory);
        SpinnerValueFactory<Integer> valueFadeOutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeOut.setValueFactory(valueFadeOutFactory);
        if (audioMedia.getFadeInDuration() != null) {
            fadeIn.getValueFactory().setValue((int) audioMedia.getFadeInDuration().toSeconds());
        }
        if (audioMedia.getFadeOutDuration() != null) {
            fadeOut.getValueFactory().setValue((int) audioMedia.getFadeOutDuration().toSeconds());
        }
    }

}
