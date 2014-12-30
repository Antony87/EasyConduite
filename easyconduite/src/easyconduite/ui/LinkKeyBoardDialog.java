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

import easyconduite.controllers.EasyconduiteController;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class manage a dialog box, wich exposes affected key, name and repeat
 * for an audio track.
 *
 * @author antony Fons
 */
public class LinkKeyBoardDialog extends Stage {

    private KeyCode chosenKey;

    private boolean nameChange = false;

    private boolean repeatChange = false;

    private final BorderPane dialogPane;

    private static final Logger logger = Logger.getLogger(LinkKeyBoardDialog.class.getName());

    private static final String PATH_FXML = "/easyconduite/ressources/trackDialog.fxml";

    public LinkKeyBoardDialog(final AudioMediaUI audioMediaUI, final EasyconduiteController controller) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML));

        dialogPane = (BorderPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Propriétes du morceaux");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);

        // initialize fields
        Scene scene = new Scene(dialogPane);
        TextField name = (TextField) scene.lookup("#nametrackfield");
        name.textProperty().set(audioMediaUI.getAudioMedia().getName());

        CheckBox repeatTrack = (CheckBox) scene.lookup("#repeattrack");
        repeatTrack.selectedProperty().setValue(audioMediaUI.getAudioMedia().getRepeat());

        Label error = (Label) scene.lookup("#error");
        TextField codeKeyboard = (TextField) scene.lookup("#keytrackfield");
        codeKeyboard.textProperty().set(KeyCodeUtil.toString(audioMediaUI.getAudioMedia().getLinkedKeyCode()));

        Button annuler = (Button) scene.lookup("#cancelbutton");
        Button ok = (Button) scene.lookup("#okbutton");

        final KeyCode existingKeyCode = audioMediaUI.getAudioMedia().getLinkedKeyCode();

        // Event Handler for cancel button
        annuler.setOnMouseClicked((MouseEvent event) -> {
            dialogStage.close();
        });

        // Event Handler for capture keycode
        codeKeyboard.setOnKeyReleased((KeyEvent event) -> {
            codeKeyboard.setText(event.getCode().getName());
            chosenKey = event.getCode();
            if (controller.isExistKeyCode(chosenKey) && chosenKey != existingKeyCode) {
                error.textProperty().setValue("Déja attribuée !");
                ok.disableProperty().set(true);
            } else {
                error.textProperty().setValue(null);
                ok.disableProperty().set(false);
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

        // Event Handler for OK button
        ok.setOnMouseClicked((MouseEvent event) -> {

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

            dialogStage.close();
        });

        dialogStage.setScene(scene);
        dialogStage.showAndWait();
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

}
