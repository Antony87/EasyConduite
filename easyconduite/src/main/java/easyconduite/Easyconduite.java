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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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
        LOG.info("Start Easyconduite with {}", stage.toString());
        
//        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//        final Configuration config = ctx.getConfiguration();
//
//        LoggerConfig loggerConfig = config.getLoggerConfig(LOG.getName());
//        LoggerConfig specificConfig = loggerConfig;
//
//        specificConfig.setLevel(Level.ERROR);
//        ctx.updateLoggers();

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/easyconduite32.png")));

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/easyconduite.fxml"));

        Pane root = loader.load(); 
        Scene scene = new Scene(root);

        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("EasyConduite 1.2");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
