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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author antony Fons
 */
public class LinkKeyBoardDialog extends VBox {

    private Stage dialogStage;

    private KeyCode chosenKey;

    private static final Logger logger = Logger.getLogger(LinkKeyBoardDialog.class.getName());

    public LinkKeyBoardDialog(final AudioMediaUI audioMediaUI, final EasyconduiteController controller) {

        super(10);

        setDialogStage(new Stage());
        getDialogStage().initModality(Modality.APPLICATION_MODAL);

        getDialogStage().initStyle(StageStyle.UTILITY);
        getDialogStage().setResizable(false);

        Scene scene = new Scene(this);
        getDialogStage().setScene(scene);

        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(10, 10, 10, 10));

        Label message = new Label("Tappez la touche");
        message.setTextAlignment(TextAlignment.CENTER);
        TextField codeKeyboard = new TextField();
        codeKeyboard.setPromptText("code");

        HBox barreBoutons = new HBox(10);
        Button annuler = new Button("annuler");

        annuler.setOnMouseClicked((MouseEvent event) -> {
            getDialogStage().close();
        });

        Button ok = new Button("ok");

        // create lstener for manage KeycodeAudioMap.
        ChangeListener<? extends KeyCode> change = (ObservableValue<? extends KeyCode> observable, KeyCode oldValue, KeyCode newValue) -> {
            
            Map<KeyCode, AudioMediaUI> mapKeyCode = controller.getKeycodesAudioMap();
            for (Map.Entry<KeyCode, AudioMediaUI> entrySet : mapKeyCode.entrySet()) {
                AudioMediaUI value = entrySet.getValue();
                if(value.getAffectedKeyCode()==newValue){
                    value.affectedKeyCodeProperty().set(KeyCode.UNDEFINED);
                }
            }
            mapKeyCode.remove(oldValue);
            
            controller.getKeycodesAudioMap().put(newValue, audioMediaUI);
            logger.log(Level.INFO, "keycodesAudioMap size {0}",mapKeyCode.size());
        };
        
        audioMediaUI.affectedKeyCodeProperty().addListener((ChangeListener) change);

        ok.setOnMouseClicked((MouseEvent event) -> {
            if (getChosenKey() != null) {
                audioMediaUI.setAffectedKeyCode(getChosenKey());
            }
            getDialogStage().close();
            audioMediaUI.affectedKeyCodeProperty().removeListener((ChangeListener)change);
        });

        barreBoutons.getChildren().addAll(annuler, ok);

        this.getChildren().addAll(message, codeKeyboard, barreBoutons);

        this.setOnKeyReleased((KeyEvent event) -> {
            codeKeyboard.setText(event.getCode().getName());
            setChosenKey(event.getCode());
            event.consume();
        });

        Show(scene);
    }

    private void Show(Scene scene) {
        getDialogStage().show();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public KeyCode getChosenKey() {
        return chosenKey;
    }

    public void setChosenKey(KeyCode chosenKey) {
        this.chosenKey = chosenKey;
    }

}
