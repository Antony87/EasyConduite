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
import easyconduite.objects.AudioMedia;
import easyconduite.ui.model.EasyConduiteAbstractDialog;
import easyconduite.util.Config;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final KeyCode existingKeyCode;

    private final CheckBox repeatTrack;

    private final TextField name;

    private final AudioMedia audioMedia;

    private final EasyconduiteController easycontroller;

    public LinkKeyBoardDialog(AudioMedia media, EasyconduiteController controller) throws IOException {

        super(PATH_FXML, "Propriétes de la piste audio", controller);

        this.audioMedia = media;

        this.easycontroller = controller;

        //audioMediaUI = anAudioMediaUI;
        // initialize fields
        name = (TextField) getScene().lookup("#nametrackfield");
        name.setText(this.audioMedia.getName());

        repeatTrack = (CheckBox) getScene().lookup("#repeattrack");
        repeatTrack.setSelected(this.audioMedia.getRepeatable());

        TextField codeKeyboard = (TextField) getScene().lookup("#keytrackfield");
        codeKeyboard.setText(KeyCodeUtil.toString(audioMedia.getKeycode()));

        existingKeyCode = this.audioMedia.getKeycode();

        // Event Handler for capture keycode
        codeKeyboard.setOnKeyReleased((KeyEvent event) -> {
            chosenKey = event.getCode();
            LOGGER.log(Level.INFO, "chosenKey {0}", chosenKey);
            boolean keyExist = false;
            keyExist = this.easycontroller.getAudioTable().getAudioMediaList().stream().anyMatch(x -> chosenKey.equals(x.getKeycode()));
            if (keyExist && !chosenKey.equals(existingKeyCode)) {

                Alert alertDeja = new Alert(Alert.AlertType.ERROR);
                alertDeja.setTitle("Erreur attribution");
                alertDeja.setHeaderText(null);
                alertDeja.setContentText("Cette touche est déja attribuée");
                alertDeja.showAndWait();
                codeKeyboard.setText(null);
            } else {
                codeKeyboard.setText(event.getCode().getName());
                LOGGER.log(Level.FINE, "Assigned Keyboard {0} for AudioMedia {1}", new Object[]{chosenKey.toString(), this.audioMedia.getFilePathName()});
            }
        });

        codeKeyboard.setOnMousePressed((MouseEvent event) -> {
            codeKeyboard.clear();
        });
    }

    @Override
    public void onClickOkButton() {
        // update Map of KeyCode
        if (chosenKey != existingKeyCode) {
            this.audioMedia.setKeycode(chosenKey);
            this.easycontroller.updateKeycodeMap(audioMedia);
        }

        this.audioMedia.setName(name.getText());
        this.audioMedia.setRepeatable(repeatTrack.selectedProperty().getValue());

        close();
    }

    @Override
    public void onClickCancelButton() {
        close();
    }

}
