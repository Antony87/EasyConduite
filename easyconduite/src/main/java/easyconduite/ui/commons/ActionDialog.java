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
package easyconduite.ui.commons;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

/**
 *
 * @author antony
 */
public class ActionDialog {
    
    private final static String INFORM_TITLE="Information";
    private final static String CONFIRM_TITLE="Demande de confirmation";
    private final static String WARNING_TITLE="Attention";
        
    public static Optional<ButtonType> showConfirmation(String header, String content) {
        final Alert a = getAlert(CONFIRM_TITLE, header, content);
        a.setAlertType(AlertType.CONFIRMATION);
        return a.showAndWait();
    }
    
    public static void showWarning(String header, String content) {
        final Alert a = getAlert(WARNING_TITLE, header, content);
        a.setAlertType(AlertType.WARNING);
        a.showAndWait();
    }
    
    public static void showInformation(String header, String content) {
        final Alert a = getAlert(INFORM_TITLE, header, content);
        a.setAlertType(AlertType.INFORMATION);
        a.showAndWait();
    }
    
    private static Alert getAlert(String title, String header, String content) {
        final Alert a = new Alert(AlertType.NONE);
        a.initModality(Modality.APPLICATION_MODAL);
        a.setHeaderText(header);
        a.setTitle(title);
        a.setContentText(content);
        return a;
    }
}
