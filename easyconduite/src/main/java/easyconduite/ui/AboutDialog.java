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

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class exposes an About dialog box.
 *
 * @author antony
 */
public class AboutDialog extends Stage {

    private final String PATH_FXML = "/fxml/aboutDialog.fxml";

    public AboutDialog() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML));

        BorderPane dialogPane = (BorderPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("A propos...");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);

        Scene scene = new Scene(dialogPane);

        Button annuler = (Button) scene.lookup("#cancelbutton");

        // Event Handler for cancel button
        annuler.setOnMouseClicked((MouseEvent event) -> {
            dialogStage.close();
        });

        Label abouttext = (Label) scene.lookup("#abouttext");
        StringBuilder stb = new StringBuilder();
        stb.append("Easyconduite v 1.1 \n");
        stb.append("antony.fons@antonyweb.net \n");
        stb.append("LGPL v2 Copyright Antony Fons 2016\n");
        stb.append("\n");
        abouttext.setText(stb.toString());

        dialogStage.setScene(scene);
        dialogStage.showAndWait();

    }

}
