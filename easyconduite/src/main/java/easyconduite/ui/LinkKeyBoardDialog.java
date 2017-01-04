/*
 * Copyright (C) 2014 antony fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.ui;

import easyconduite.util.KeyCodeUtil;
import easyconduite.controllers.EasyconduiteController;
import easyconduite.ui.model.EasyConduiteAbstractDialog;
import easyconduite.util.Config;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * This class manage a dialog box, wich exposes affected key, name and repeat
 * for an audio track.
 *
 * @author antony Fons
 */
public class LinkKeyBoardDialog extends EasyConduiteAbstractDialog {
    
    private static final String CLASSNAME = LinkKeyBoardDialog.class.getName();

    private static final Logger LOGGER = Config.getCustomLogger(CLASSNAME);

    private static final String PATH_FXML = "/fxml/trackDialog.fxml";

    private KeyCode chosenKey;

    private final AudioMediaUI audioMediaUI;

    private final KeyCode existingKeyCode;

    private final CheckBox repeatTrack;

    private final TextField name;

    private boolean nameChange = false;

    private boolean repeatChange = false;

    public LinkKeyBoardDialog(final AudioMediaUI anAudioMediaUI, final EasyconduiteController controller) throws IOException {

        super(PATH_FXML, "Propriétes de la piste audio", controller);

        audioMediaUI = anAudioMediaUI;

        // initialize fields
        name = (TextField) getScene().lookup("#nametrackfield");
        name.textProperty().set(audioMediaUI.getAudioMedia().getName());

        repeatTrack = (CheckBox) getScene().lookup("#repeattrack");
        repeatTrack.selectedProperty().setValue(audioMediaUI.getAudioMedia().getRepeat());

        TextField codeKeyboard = (TextField) getScene().lookup("#keytrackfield");
        codeKeyboard.textProperty().set(KeyCodeUtil.toString(audioMediaUI.getAudioMedia().getLinkedKeyCode()));

        existingKeyCode = audioMediaUI.getAudioMedia().getLinkedKeyCode();

        // Event Handler for capture keycode
        codeKeyboard.setOnKeyReleased((KeyEvent event) -> {
            chosenKey = event.getCode();
            if (controller.isExistKeyCode(chosenKey) && chosenKey != existingKeyCode) {
                Alert alertDeja = new Alert(Alert.AlertType.ERROR);
                alertDeja.setTitle("Erreur attribution");
                alertDeja.setHeaderText(null);
                alertDeja.setContentText("Cette touche est déja attribuée");
                alertDeja.showAndWait();
            } else {
                codeKeyboard.setText(event.getCode().getName());
                LOGGER.log(Level.FINE, "Assigned Keyboard {0} for AudioMedia {1}",new Object[]{chosenKey.toString(),audioMediaUI.getAudioMedia().getFilePathName()});
            }
        });

        codeKeyboard.setOnMousePressed((MouseEvent event) -> {
            codeKeyboard.clear();
        });

        name.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.equals(oldValue)) {
                setNameChange(true);
            }
        });

        repeatTrack.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!Objects.equals(newValue, oldValue)) {
                setRepeatChange(true);
            }
        });

    }

    public final boolean isNameChange() {
        return nameChange;
    }

    public final void setNameChange(boolean nameChange) {
        this.nameChange = nameChange;
    }

    public final boolean isRepeatChange() {
        return repeatChange;
    }

    public final void setRepeatChange(boolean repeatChange) {
        this.repeatChange = repeatChange;
    }

    @Override
    public void onClickOkButton() {
        // update Map of KeyCode
        if (chosenKey != existingKeyCode) {
            audioMediaUI.updateAffectedKeyCode(chosenKey);
//                audioMediaUI.affectedKeyCodeProperty().set(chosenKey);
        }

        if (isRepeatChange()) {
            audioMediaUI.updateRepeat(repeatTrack.selectedProperty().getValue());
        }

        if (isNameChange()) {
            audioMediaUI.updateName(name.getText());
        }
        close();
    }

    @Override
    public void onClickCancelButton() {
        close();
    }

}
