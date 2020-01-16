package easyconduite.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import easyconduite.exception.EasyconduiteException;
import easyconduite.exception.PersistenceException;
import easyconduite.fixture.EasyProjectFixture;
import easyconduite.objects.EasyConduiteProperties;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.project.MediaProject;
import easyconduite.tools.EasyConduitePropertiesHandler;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void testSerialization() throws IOException {

        MediaProject project = EasyProjectFixture.getValidProject();
        AudioVideoMedia media1 = (AudioVideoMedia) project.getEasyMediaList().get(0);
                //PersistenceHelper.saveToXml(project, new File("src/test/resources/test.xml"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.WRAPPER_ARRAY);

        String jsonDataString = mapper.writeValueAsString(project);
        System.out.println(jsonDataString);

        MediaProject project2 = mapper.readValue(jsonDataString,MediaProject.class);
        AudioVideoMedia media2 = (AudioVideoMedia) project2.getEasyMediaList().get(0);

        assertEquals(media1.getVolume(),media2.getVolume());
        assertEquals(media1.getMediaFile(),media2.getMediaFile());
        assertEquals(media1.getDuration(),media2.getDuration());
        assertEquals(media1.getKeycode(),media2.getKeycode());

    }

    @Test
    public void testPersistenceHelper_Media() throws IOException {

        MediaProject project = EasyProjectFixture.getValidProject();
        AudioVideoMedia media1 = (AudioVideoMedia) project.getEasyMediaList().get(0);
        PersistenceHelper.saveToJson(project,new File("C:\\Users\\V902832\\IdeaProjects\\EasyConduite\\easyconduite\\src\\test\\resources\\test.ecp"));

        MediaProject project2 = PersistenceHelper.openFromJson(new File("C:\\Users\\V902832\\IdeaProjects\\EasyConduite\\easyconduite\\src\\test\\resources\\test.ecp"), MediaProject.class);
        AudioVideoMedia media2 = (AudioVideoMedia) project2.getEasyMediaList().get(0);
        assertEquals(media1.getVolume(),media2.getVolume());
        assertEquals(media1.getMediaFile(),media2.getMediaFile());
        assertEquals(media1.getDuration(),media2.getDuration());
        assertEquals(media1.getKeycode(),media2.getKeycode());
    }

    @Test
    public void testPersistenceHelper_EasyconduiteProperties() throws EasyconduiteException {
        EasyConduiteProperties properties = EasyConduitePropertiesHandler.getInstance().getApplicationProperties();
    }

}
