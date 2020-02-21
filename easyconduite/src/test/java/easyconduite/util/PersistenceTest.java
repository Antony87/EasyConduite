package easyconduite.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import easyconduite.exception.EasyconduiteException;
import easyconduite.fixture.EasyProjectFixture;
import easyconduite.objects.EasyConduiteProperties;
import easyconduite.objects.media.AudioMedia;
import easyconduite.objects.project.MediaProject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistenceTest {

    MediaProject projet;

    @Test
    public void testSerialization() throws IOException {

        MediaProject project = EasyProjectFixture.getValidProject();
        AudioMedia media1 = (AudioMedia) project.getEasyMediaList().get(0);
                //PersistenceHelper.saveToXml(project, new File("src/test/resources/test.xml"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.WRAPPER_ARRAY);

        String jsonDataString = mapper.writeValueAsString(project);
        System.out.println(jsonDataString);

        MediaProject project2 = mapper.readValue(jsonDataString,MediaProject.class);
        AudioMedia media2 = (AudioMedia) project2.getEasyMediaList().get(0);

        assertEquals(media1.getVolume(),media2.getVolume());
        assertEquals(media1.getResourcePath(),media2.getResourcePath());
        assertEquals(media1.getDuration(),media2.getDuration());
        assertEquals(media1.getKeycode(),media2.getKeycode());

    }

    @Test
    public void testPersistenceHelper_Media() throws IOException {

        MediaProject project = EasyProjectFixture.getValidProject();
        AudioMedia media1 = (AudioMedia) project.getEasyMediaList().get(0);
        PersistenceHelper.saveToJson(project,new File("C:\\Users\\V902832\\IdeaProjects\\EasyConduite\\easyconduite\\src\\test\\resources\\test.ecp"));

        MediaProject project2 = PersistenceHelper.openFromJson(new File("C:\\Users\\V902832\\IdeaProjects\\EasyConduite\\easyconduite\\src\\test\\resources\\test.ecp"), MediaProject.class);
        AudioMedia media2 = (AudioMedia) project2.getEasyMediaList().get(0);
        assertEquals(media1.getVolume(),media2.getVolume());
        assertEquals(media1.getResourcePath(),media2.getResourcePath());
        assertEquals(media1.getDuration(),media2.getDuration());
        assertEquals(media1.getKeycode(),media2.getKeycode());
    }

    @Test
    public void testPersistenceHelper_EasyconduiteProperties() throws EasyconduiteException {
        EasyConduiteProperties properties = EasyConduitePropertiesHandler.getInstance().getApplicationProperties();
    }

}
