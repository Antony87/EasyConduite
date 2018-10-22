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
package easyconduite.view;

import easyconduite.controllers.PreferencesController;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author antony
 */
public class PreferencesUI extends Stage {
    
    static final Logger LOG = LogManager.getLogger(PreferencesUI.class);

    private final String PATH_FXML = "/fxml/preferences.fxml";

    public PreferencesUI(ResourceBundle bundle) throws IOException {
        super();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML),bundle);
        BorderPane dialogPane = loader.<BorderPane>load();
        
        PreferencesController controler = loader.getController();
        
        this.setTitle("Preferences");
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);
        this.setScene(new Scene(dialogPane));
        
    }

}
