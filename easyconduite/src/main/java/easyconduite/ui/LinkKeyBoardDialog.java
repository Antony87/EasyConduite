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
package easyconduite.ui;

import easyconduite.util.KeyCodeUtil;
import easyconduite.controllers.EasyconduiteController;
import easyconduite.objects.AudioMedia;
import easyconduite.ui.model.EasyConduiteAbstractDialog;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class manage a dialog box, wich exposes affected key, name and repeat
 * for an audio track.
 *
 * @author antony Fons
 */
public class LinkKeyBoardDialog extends EasyConduiteAbstractDialog {

    static final Logger LOG = LogManager.getLogger(LinkKeyBoardDialog.class);

    private static final String PATH_FXML = "/fxml/trackDialog.fxml";

    private KeyCode pressedKeyCode;

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

        // Event Handler for capture keycode
        codeKeyboard.setOnKeyReleased((KeyEvent event) -> {
            LOG.trace("Key {} was pressed", event.getCode());
            codeKeyboard.clear();
            pressedKeyCode = event.getCode();
            if (pressedKeyCode != this.audioMedia.getKeycode()) {
                boolean keyExist = this.easycontroller.isKeyCodeExist(pressedKeyCode);
                if (keyExist) {
                    Alert alertDeja = new Alert(Alert.AlertType.ERROR);
                    alertDeja.setTitle("Erreur attribution");
                    alertDeja.setHeaderText(null);
                    alertDeja.setContentText("Cette touche est déja attribuée");
                    alertDeja.showAndWait();
                    codeKeyboard.setText(null);
                    pressedKeyCode = null;
                } else {
                    codeKeyboard.setText(event.getCode().getName());
                }
            } else {
                codeKeyboard.setText(KeyCodeUtil.toString(audioMedia.getKeycode()));
                pressedKeyCode = null;
            }

        });

        codeKeyboard.setOnMousePressed((MouseEvent event) -> {
            codeKeyboard.clear();
            pressedKeyCode = null;
        });
    }

    @Override
    public void onClickOkButton() {
        // update Map of KeyCode
        if (pressedKeyCode != audioMedia.getKeycode()) {
            this.audioMedia.setKeycode(pressedKeyCode);
            LOG.debug("Key {} set for AudioMedia {}", pressedKeyCode, this.audioMedia);
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
