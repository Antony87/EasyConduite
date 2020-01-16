package easyconduite.objects;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EasyConduitePropertiesTest {

    private EasyConduiteProperties props;

    @BeforeEach
    public void setup(){
        props = new EasyConduiteProperties();
    }

    @Test
    public void testChangeListener(){
        props.setWindowWith(200);
        props.setWindowHeight(300);

    }

    @Test
    public void testSerialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.WRAPPER_ARRAY);
        props.setWindowWith(200);
        props.setWindowHeight(300);
        props.setLogLevel(Level.OFF);
        props.setLocale(new Locale(System.getProperty("user.language"), System.getProperty("user.country")));
        props.setLastFileProject(new File("C:\\Users\\V902832\\IdeaProjects\\EasyConduite\\easyconduite\\target\\test-classes\\Alarme.wav"));
        props.setLastImportDir(Path.of("C:\\Users\\V902832\\IdeaProjects\\EasyConduite\\easyconduite\\target\\test-classes"));
        final String xmlProps = mapper.writeValueAsString(props);
        System.out.println(xmlProps);

        EasyConduiteProperties props2 = mapper.readValue(xmlProps,EasyConduiteProperties.class);

        assertEquals(props.getLogLevel(),props2.getLogLevel());
        assertEquals(props.getLastFileProject(),props2.getLastFileProject());
        assertEquals(props.getLastImportDir(),props2.getLastImportDir());

    }

}