/*
 * Copyright (C) 2014 antony Fons
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
import easyconduite.objects.AudioTable;
import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author antony Fons
 */
public class ParamConduiteDialog extends Stage {

    private Duration duration;
    
    private TextField durationText;

    private final BorderPane dialogPane;

    private static final Logger logger = Logger.getLogger(ParamConduiteDialog.class.getName());

    private static final String PATH_FXML = "/easyconduite/ressources/conduiteDialog.fxml";

    public ParamConduiteDialog(final AudioTable audioTable, final EasyconduiteController controller) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML));

        dialogPane = (BorderPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Propriétes du spectacle");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);

        // initialize fields
        Scene scene = new Scene(dialogPane);
        durationText = (TextField) scene.lookup("#durationField");

        durationText.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!isParsableToDuration(newValue)){
                    durationText.setStyle("-fx-border-color:red");
                }else{
                    durationText.setStyle("");
                }

            }
        });

        Button annuler = (Button) scene.lookup("#cancelbutton");
        Button ok = (Button) scene.lookup("#okbutton");

        // Event Handler for cancel button
        annuler.setOnMouseClicked((MouseEvent event) -> {
            dialogStage.close();
        });

        dialogStage.setScene(scene);
        dialogStage.showAndWait();

    }

    private boolean isParsableToDuration(String textField) {

        String[] values = textField.split(":");

        if (null != values && values.length == 2) {
            StringBuilder stb = new StringBuilder();
            stb.append("PT");
            stb.append(values[0]);
            stb.append("H");
            stb.append(values[1]);
            stb.append("M");

            try {
                Duration.parse(stb);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }

        }

        return false;

    }

}
