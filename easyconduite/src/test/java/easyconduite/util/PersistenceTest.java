package easyconduite.util;

import com.thoughtworks.xstream.XStream;
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.EasyMedia;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.MediaProject;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    public void testSerialization() {

//        EasyMedia media=new AudioVideoMedia(new File("src/test/resources/Alarme.wav"));
        EasyMedia media = MediaFactory.getAudioVisualMedia(new File("src/test/resources/Alarme.wav"));
        ((AudioVideoMedia) media).setDuration(new Duration(2000));
        ((AudioVideoMedia) media).setVolume(100L);
        ((AudioVideoMedia) media).setKeycode(KeyCode.E);
        ((AudioVideoMedia) media).setFadeInDuration(new Duration(1200));
        ((AudioVideoMedia) media).setFadeOutDuration(Duration.INDEFINITE);

        projet.getEasyMediaList().add(media);

        XStream xstream = new XStream();

        String toto = xstream.toXML(projet);
        System.out.println(toto);
        MediaProject deserializeProect = (MediaProject) xstream.fromXML(toto);
        EasyMedia media2 = deserializeProect.getEasyMediaList().get(0);
        try {
            ((AudioVideoMedia) media2).initPlayer();
            ((AudioVideoMedia) media2).getPlayer().play();
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
        assertNotNull(((AudioVideoMedia) media2).getPlayer());
        assertEquals("test", deserializeProect.getName());
        assertEquals(Duration.INDEFINITE, ((AudioVideoMedia) media2).getFadeOutDuration());
        assertEquals(KeyCode.E, ((AudioVideoMedia) media2).getKeycode());

    }
}
