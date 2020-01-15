package easyconduite.util;

import easyconduite.exception.PersistenceException;
import easyconduite.fixture.EasyProjectFixture;
import easyconduite.objects.project.MediaProject;
import easyconduite.tools.PersistenceHelper;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class PersistenceTest {

    MediaProject projet;
    Scene scene;

    @Start
    private void start(Stage stage) {
        projet = new MediaProject();
        projet.setName("test");
        scene = new Scene(new StackPane(), 100, 100);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testSerialization() throws PersistenceException {
        MediaProject project = EasyProjectFixture.getValidProject();
        PersistenceHelper.saveToXml(project, new File("src/test/resources/test.xml"));

        MediaProject project2 = PersistenceHelper.openFromXml(new File("src/test/resources/test.xml"));

        assertEquals(project.getName(),project2.getName());
        assertEquals(project.getEasyMediaList().get(0),project2.getEasyMediaList().get(0));

    }
}
