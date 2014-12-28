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
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
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

    private final BorderPane dialogPane;

    private static final Logger logger = Logger.getLogger(LinkKeyBoardDialog.class.getName());

    private static final String PATH_FXML = "/easyconduite/ressources/trackDialog.fxml";

    public LinkKeyBoardDialog(final AudioMediaUI audioMediaUI, final EasyconduiteController controller) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML));

        dialogPane = (BorderPane) loader.load();
        
        loader = null;
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Propriétes du morceaux");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);

        final KeyCode existingKeyCode = audioMediaUI.getAffectedKeyCode();

        Scene scene = new Scene(dialogPane);

        TextField name = (TextField) scene.lookup("#nametrackfield");

        Label error = (Label) scene.lookup("#error");

        name.textProperty().bindBidirectional(audioMediaUI.nameProperty());
        TextField codeKeyboard = (TextField) scene.lookup("#keytrackfield");

        codeKeyboard.textProperty().set(KeyCodeUtil.toString(audioMediaUI.getAffectedKeyCode()));

        Button annuler = (Button) scene.lookup("#cancelbutton");
        Button ok = (Button) scene.lookup("#okbutton");
        
        CheckBox repeatTrack = (CheckBox) scene.lookup("#repeattrack");
        repeatTrack.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                audioMediaUI.repeatProperty().setValue(newValue);
            }
        });

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

        // Event Handler for OK button
        ok.setOnMouseClicked((MouseEvent event) -> {

            // update name of AudioMedia
            audioMediaUI.getAudioMedia().setName(audioMediaUI.nameProperty().getValue());

            // update Map of KeyCode
            if (chosenKey != existingKeyCode) {
                audioMediaUI.affectedKeyCodeProperty().set(chosenKey);
            }
            dialogStage.close();
        });

        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

}
