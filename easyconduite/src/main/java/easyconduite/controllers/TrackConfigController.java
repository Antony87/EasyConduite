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

import easyconduite.exception.EasyconduiteException;
import easyconduite.model.DialogAbstractController;
import easyconduite.model.EasyMedia;
import easyconduite.model.IEasyMediaUI;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.util.EasyConduitePropertiesHandler;
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
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
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

    private KeyCode newKeyCode;

    private List<IEasyMediaUI> mediaUIList;

    @FXML
    private BorderPane trackConfigPane;

    @FXML
    private TextField nametrackfield;

    @FXML
    private TextField keytrackfield;

    @FXML
    private CheckBox loopTrack;

    @FXML
    private Spinner<Integer> fadeInSpinner;

    @FXML
    private Spinner<Integer> fadeOutSpinner;

    private AudioMediaUI mediaUI;

    public TrackConfigController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void handleClickOk(MouseEvent event) {

        final Integer iValueFadeOut = fadeOutSpinner.getValue();
        final Integer iValueFadeIn = fadeInSpinner.getValue();
        final EasyMedia media = mediaUI.getEasyMedia();
        media.setLoppable(loopTrack.isSelected());
        media.setName(nametrackfield.getText());
        if(newKeyCode!=null){
            media.setKeycode(newKeyCode);
        }
        if(media instanceof AudioVideoMedia){
            ((AudioVideoMedia) media).setFadeInDuration(new Duration(iValueFadeIn));
            ((AudioVideoMedia) media).setFadeOutDuration(new Duration(iValueFadeOut));
        }
        mediaUI.actualizeUI();

        this.close(trackConfigPane);
    }

    @FXML
    private void handleClickCancel(MouseEvent event) {
        this.close(trackConfigPane);
    }

    @FXML
    private void handleClickKeyField(MouseEvent event) {
        keytrackfield.clear();
    }

    @FXML
    private void handleKeyReleasedTrack(KeyEvent event) {
        final KeyCode typedKeycode = event.getCode();
        if (isKeyCodeExist(typedKeycode)) {
            keytrackfield.clear();
            try {
                final ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
                ActionDialog.showWarning(String.format(bundle.getString(KEY_ASSIGN_ERROR), KeyCodeHelper.toString(typedKeycode)), bundle.getString(KEY_ASSIGN_OTHER));
            } catch (EasyconduiteException e) {
                //FIXME
                e.printStackTrace();
            }
        } else {
            final AudioVideoMedia audioMedia = (AudioVideoMedia) this.mediaUI.getEasyMedia();
            if (typedKeycode != audioMedia.getKeycode()) {
                this.newKeyCode = typedKeycode;
                keytrackfield.setText(KeyCodeHelper.toString(typedKeycode));
            }
        }
    }

    private boolean isKeyCodeExist(KeyCode keyCode) {
        for (IEasyMediaUI ui : mediaUIList) {
            final KeyCode code = ui.getEasyMedia().getKeycode();
            if (code != null && code.equals(keyCode)) return true;
        }
        return false;
    }

    public void setMediaUI(IEasyMediaUI mediaUI) {
        if (mediaUI != null) {
            this.mediaUI = (AudioMediaUI) mediaUI;
            final EasyMedia media = this.mediaUI.getEasyMedia();
            nametrackfield.setText(media.getName());
            keytrackfield.setText(KeyCodeHelper.toString(media.getKeycode()));
            loopTrack.setSelected(media.getLoppable());
            initializeSpinners((AudioVideoMedia) media);
        }
    }

    public void setMediaUIList(List<IEasyMediaUI> mediaUIList) {
        this.mediaUIList = mediaUIList;
    }

    private void initializeSpinners(AudioVideoMedia audioMedia) {

        final SpinnerValueFactory<Integer> valueFadeInFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeInSpinner.setValueFactory(valueFadeInFactory);
        final SpinnerValueFactory<Integer> valueFadeOutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeOutSpinner.setValueFactory(valueFadeOutFactory);
        if (audioMedia.getFadeInDuration() != null) {
            fadeInSpinner.getValueFactory().setValue((int) audioMedia.getFadeInDuration().toSeconds());
        }
        if (audioMedia.getFadeOutDuration() != null) {
            fadeOutSpinner.getValueFactory().setValue((int) audioMedia.getFadeOutDuration().toSeconds());
        }
    }

}
