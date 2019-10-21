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
package easyconduite;

import easyconduite.controllers.MainController;
import easyconduite.objects.ApplicationProperties;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.tools.Constants;
import easyconduite.tools.LoggingHelper;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author A. Fons
 */
public class Easyconduite extends Application {

    static final Logger LOG = LogManager.getLogger(Easyconduite.class);

    @Override
    public void start(Stage stage) throws Exception {
        
        ApplicationProperties applicationProperties = ApplicationPropertiesHelper.getInstance().getProperties();
        ResourceBundle localeBundle = ResourceBundle.getBundle(Constants.RESOURCE_BASENAME, applicationProperties.getLocale());

        // Pass args to describe using logging context :
        // --context=user
        // OR --logctx=dev (default)
        final Map<String, String> arguments = this.getParameters().getNamed();
        // if lgctx=dev
        if (arguments.containsKey("logctx") && arguments.get("logctx").equals("dev")) {
            LoggingHelper.setLog4jLevel(Level.ALL);
        }else{
            LoggingHelper.setLog4jLevel(applicationProperties.getLogLevel());
        }
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/easyconduite32.png")));
        
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/easyconduite.fxml"), localeBundle);
        
        Pane root = loader.load();
        Scene scene = new Scene(root);
        
        MainController controler = loader.getController();
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            controler.menuQuit(new ActionEvent());
            event.consume();
        });
                
        stage.setScene(scene);

        ApplicationPropertiesHelper.getInstance().applyProperties(stage);
        applicationProperties.setCurrentWindow(stage);

        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
