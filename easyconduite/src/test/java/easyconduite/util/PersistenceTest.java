package easyconduite.util;

import com.thoughtworks.xstream.XStream;
import easyconduite.exception.EasyconduiteException;
import easyconduite.fixture.EasyProjectFixture;
import easyconduite.model.EasyMedia;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.MediaProject;
import easyconduite.tools.Constants;
import easyconduite.tools.PersistenceHelper;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    public void testSerialization() throws IOException, ClassNotFoundException {
        MediaProject project = EasyProjectFixture.getValidProject();
        PersistenceHelper.saveXml(project, new File("src/test/resources/test.xml"));

        MediaProject project2 = PersistenceHelper.openXml(new File("src/test/resources/test.xml"));

        assertEquals(project.getName(),project2.getName());
        assertEquals(project.getEasyMediaList().get(0),project2.getEasyMediaList().get(0));

    }
}
