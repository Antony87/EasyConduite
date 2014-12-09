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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class manage a dialog box, wich exposes affected key, name and repeat for an audio track.
 *
 * @author antony Fons
 */
public class LinkKeyBoardDialog extends Stage {

    private KeyCode chosenKey;

    private final BorderPane dialogPane;

    private static final Logger logger = Logger.getLogger(LinkKeyBoardDialog.class.getName());

    private static final String PATH_FXML = "/easyconduite/ressources/trackDialog.fxml";

    public LinkKeyBoardDialog(final AudioMediaUI audioMediaUI, final EasyconduiteController controller) throws IOException {

        final FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML));

        dialogPane = (BorderPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Propriétes du morceaux");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);

        Scene scene = new Scene(dialogPane);

        TextField name = (TextField) scene.lookup("#nametrackfield");
        name.textProperty().bindBidirectional(audioMediaUI.nameProperty());
        TextField codeKeyboard = (TextField) scene.lookup("#keytrackfield");
        final KeyCode codeExistant = audioMediaUI.getAffectedKeyCode();
        if (codeExistant != KeyCode.UNDEFINED) {
            codeKeyboard.setText(codeExistant.getName());
        } else {
            codeKeyboard.setText("");
            codeKeyboard.setPromptText("pressez une touche");
        }
        Button annuler = (Button) scene.lookup("#cancelbutton");
        Button ok = (Button) scene.lookup("#okbutton");

        // Event Handler for cancel button
        annuler.setOnMouseClicked((MouseEvent event) -> {
            dialogStage.close();
        });

        // Event Handler for capture keycode
        codeKeyboard.setOnKeyReleased((KeyEvent event) -> {
            codeKeyboard.clear();
            codeKeyboard.setText(event.getCode().getName());
            setChosenKey(event.getCode());
        });

        // Event Handler for OK button
        ok.setOnMouseClicked((MouseEvent event) -> {
            if (getChosenKey() != KeyCode.UNDEFINED && null != getChosenKey()) {
                controller.updateKeyCodetoMap(getChosenKey(), audioMediaUI);
            }
            dialogStage.close();
        });

        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private KeyCode getChosenKey() {
        return chosenKey;
    }

    private void setChosenKey(KeyCode chosenKey) {
        this.chosenKey = chosenKey;
    }

}
