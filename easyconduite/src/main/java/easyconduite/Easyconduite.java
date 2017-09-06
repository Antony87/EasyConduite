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

import easyconduite.controllers.EasyconduiteController;
import easyconduite.objects.EasyconduiteProperty;
import easyconduite.util.Constants;
import easyconduite.util.EasyConduitePropertiesHandler;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 *
 * @author A. Fons
 */
public class Easyconduite extends Application {

    private ResourceBundle localeBundle;

    static final Logger LOG = LogManager.getLogger(Easyconduite.class);

    @Override
    public void start(Stage stage) throws Exception {
        initUserData(stage);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/easyconduite32.png")));

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/easyconduite.fxml"), localeBundle);

        Pane root = loader.load();
        Scene scene = new Scene(root);

        EasyconduiteController controler = loader.getController();

        stage.setOnCloseRequest((WindowEvent event) -> {
            controler.handleQuit(new ActionEvent());
            event.consume();
        });

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

    /**
     * This method sets log4j level for easyconduite logger.
     *
     * @param level
     */
    public static void setLog4jLevel(Level level) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final LoggerConfig loggerConfig = config.getLoggerConfig(LOG.getName());
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public ResourceBundle getLocaleBundle() {
        return localeBundle;
    }

    private void initUserData(Stage stage) {
        final EasyconduiteProperty userdatas = EasyConduitePropertiesHandler.getInstance().getProperties();
        setLog4jLevel(Level.ALL);
        localeBundle=ResourceBundle.getBundle(Constants.RESOURCE_BASENAME);
        stage.setWidth(userdatas.getWindowWith());
        stage.setHeight(userdatas.getWindowHeight());
    }

}
