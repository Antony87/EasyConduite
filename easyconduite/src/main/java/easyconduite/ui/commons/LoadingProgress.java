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

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author antony
 */
public class LoadingProgress extends Stage {
    
    private final String PATH_FXML = "/fxml/progressBarPane.fxml";
    
    private static final Logger LOG = LogManager.getLogger(LoadingProgress.class);
    
    public LoadingProgress() {
        super();

//final ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FXML));
        
        StackPane dialogPane = null;
        try {
            dialogPane = loader.<StackPane>load();
        } catch (IOException ex) {
            LOG.error(ex);
        }
        
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setAlwaysOnTop(true);
        
        Scene scene = new Scene(dialogPane);
        this.setScene(scene);
        this.show();
    }
    
}
