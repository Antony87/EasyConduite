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
package easyconduite.view.controls;

import easyconduite.exception.EasyconduiteException;
import easyconduite.util.EasyConduitePropertiesHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 *
 * @author antony
 */
public class ActionDialog {
            
    public static Optional<ButtonType> showConfirmation(String header, String content) {
        try {
            final ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
        final Alert a = createAlert("dialog.confirmation.title", header, content);
        a.getButtonTypes().add(ButtonType.NO);
        a.getButtonTypes().add(ButtonType.YES);
        a.setAlertType(AlertType.CONFIRMATION);
        return a.showAndWait();
    }
    
    public static void showWarning(String header, String content) {
        final Alert a = createAlert("dialog.warning.title", header, content);
        a.setAlertType(AlertType.WARNING);
        a.showAndWait();
    }
    
    public static void showInformation(String header, String content) {
        final Alert a = createAlert("dialog.information.title", header, content);
        a.setAlertType(AlertType.INFORMATION);
        a.showAndWait();
    }

    public static void showError(String header, String content){
        final Alert a = createAlert("dialog.error.header", header, content);
        a.setAlertType(AlertType.ERROR);
        a.showAndWait();
    }

    public static void showException(String header, String content, Exception e){
        final Alert a = createAlert("dialog.exception.title", header, content);
        a.setAlertType(AlertType.ERROR);
        Label label = new Label("The exception stacktrace was:");

        String stb = Arrays.toString(e.getStackTrace());

        TextArea textArea = new TextArea(stb);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        a.getDialogPane().setExpandableContent(expContent);
        a.showAndWait();
    }
    
    private static Alert createAlert(String key_title, String header, String content) {
        ResourceBundle bundle = null;
        try {
            bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
        final Alert a = new Alert(AlertType.NONE);
        a.initModality(Modality.APPLICATION_MODAL);
        a.setHeaderText(header);
        a.setTitle(bundle.getString(key_title));
        a.setContentText(content);
        return a;
    }
}
