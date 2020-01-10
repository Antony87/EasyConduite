package easyconduite.util;

import com.thoughtworks.xstream.XStream;
import easyconduite.model.EasyMedia;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.MediaProject;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PersistenceTest {

    MediaProject projet;

    @BeforeEach
    public void setUp() throws Exception {
        projet=new MediaProject();
        projet.setName("test");
    }

    @Test
    public void testSerialization()  {

//        EasyMedia media=new AudioVideoMedia(new File("src/test/resources/Alarme.wav"));
        EasyMedia media= MediaFactory.getAudioVisualMedia(new File("src/test/resources/Alarme.wav"));
        ((AudioVideoMedia)media).setDuration(new Duration(2000));
        ((AudioVideoMedia)media).setVolume(20L);
        ((AudioVideoMedia)media).setKeycode(KeyCode.E);
        ((AudioVideoMedia)media).setFadeInDuration(new Duration(1200));
        ((AudioVideoMedia)media).setFadeOutDuration(Duration.INDEFINITE);

        projet.getEasyMediaList().add(media);

        XStream xstream = new XStream();

        String toto =xstream.toXML(projet);
        System.out.println(toto);
        MediaProject deserializeProect = (MediaProject)xstream.fromXML(toto);
        EasyMedia media2 = deserializeProect.getEasyMediaList().get(0);

        assertEquals("test",deserializeProect.getName());
        assertEquals(Duration.INDEFINITE,((AudioVideoMedia)media2).getFadeOutDuration());
        assertEquals(KeyCode.E,((AudioVideoMedia)media2).getKeycode());


    }
}
