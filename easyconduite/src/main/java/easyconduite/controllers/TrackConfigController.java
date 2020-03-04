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
import easyconduite.model.AbstractMedia;
import easyconduite.model.SpecificConfigurable;
import easyconduite.model.UIMediaPlayable;
import easyconduite.objects.media.AudioMedia;
import easyconduite.objects.media.RemoteMedia;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.KeyCodeHelper;
import easyconduite.view.controls.ActionDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class is the controller for TrackConfigDialog.
 *
 * @author antony
 */
public class TrackConfigController implements Initializable {

    static final Logger LOG = LogManager.getLogger(TrackConfigController.class);
    private static final String KEY_ASSIGN_ERROR = "trackconfigcontroller.key.error";
    private static final String KEY_ASSIGN_OTHER = "trackconfigcontroller.key.other";

    private final ResourceBundle locale;

    private KeyCode newKeyCode;

    private List<UIMediaPlayable> mediaUIList;

    private SpecificConfigurable secondaryController;

    @FXML
    private VBox trackConfigVbox;

    @FXML
    private TextField nametrackfield;

    @FXML
    private TextField keytrackfield;

    @FXML
    private CheckBox loopTrack;

    @FXML
    private StackPane specificPane;

    private UIMediaPlayable mediaUI;

    public TrackConfigController() throws EasyconduiteException {
        super();
        locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void handleClickOk(MouseEvent event) {

        final AbstractMedia media = mediaUI.getAbstractMedia();
        media.setLoppable(loopTrack.isSelected());
        media.setName(nametrackfield.getText());
        if (newKeyCode != null) {
            media.setKeycode(newKeyCode);
        }
        if (secondaryController != null) secondaryController.updateSpecificMedia(media);
        mediaUI.actualizeUI();
        LOG.debug("Media changed {}", media.toString());
        this.close();

    }

    @FXML
    private void handleClickCancel(MouseEvent event) {
        this.close();
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
            final AbstractMedia media = this.mediaUI.getAbstractMedia();
            if (typedKeycode != media.getKeycode()) {
                this.newKeyCode = typedKeycode;
                keytrackfield.setText(KeyCodeHelper.toString(typedKeycode));
            }
        }
    }

    private boolean isKeyCodeExist(KeyCode keyCode) {
        for (UIMediaPlayable ui : mediaUIList) {
            final KeyCode code = ui.getAbstractMedia().getKeycode();
            if (code != null && code.equals(keyCode)) return true;
        }
        return false;
    }

    public void initUIConfig(UIMediaPlayable mediaUI, List<UIMediaPlayable> otherMediaUIs) {
        this.mediaUI = mediaUI;
        this.mediaUIList = otherMediaUIs;
        if (mediaUI != null) {
            this.mediaUI = mediaUI;
            final AbstractMedia media = this.mediaUI.getAbstractMedia();
            nametrackfield.setText(media.getName());
            keytrackfield.setText(KeyCodeHelper.toString(media.getKeycode()));
            loopTrack.setSelected(media.getLoppable());

            secondaryController = null;
            if (media instanceof AudioMedia) {
                final String secondaryFxml = "/fxml/secondary/specAudioConfig.fxml";
                secondaryController = new AudioConfigController((AudioMedia) media);
                buildSpecificPane(secondaryController, secondaryFxml);
            } else if (media instanceof RemoteMedia) {
                secondaryController = new RemoteConfigController((RemoteMedia) media);
                final String secondaryFxml = "/fxml/secondary/specRemoteConfig.fxml";
                buildSpecificPane(secondaryController, secondaryFxml);
            } else {
            }
        }
    }

    private void close(){
        final Stage stage = (Stage) trackConfigVbox.getScene().getWindow();
        stage.close();
    }

    private void buildSpecificPane(SpecificConfigurable secondaryController, String secondaryFxml) {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(secondaryFxml), locale);
        try {
            loader.setController(secondaryController);
            Parent specificParent = loader.load();
            specificPane.getChildren().add(specificParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
