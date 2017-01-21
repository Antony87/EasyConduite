/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyconduite;

import easyconduite.controllers.EasyconduiteController;
import easyconduite.objects.AudioTable;
import easyconduite.ui.Chrono;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author A. Fons
 */
public class Easyconduite extends Application {
    
    static final Logger LOG = LogManager.getLogger(Easyconduite.class);

    @Override
    public void start(Stage stage) throws Exception {
        LOG.info("Start Easyconduite with {}",stage.toString());
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/easyconduite32.png")));

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/easyconduite.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        EasyconduiteController controller = loader.getController();

        scene.getStylesheets().add("/styles/Styles.css");
        Label ltimer = (Label) scene.lookup("#timer");
        Timeline timeline = Chrono.getTimeline(ltimer);
        controller.setTimeline(timeline);
        controller.setScene(scene);

        stage.setTitle("EasyConduite 1.1");
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
