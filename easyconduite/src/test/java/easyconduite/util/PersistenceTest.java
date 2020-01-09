package easyconduite.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import easyconduite.Easyconduite;
import easyconduite.model.EasyMedia;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.project.MediaTable;
import javafx.util.Duration;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;


public class PersistenceTest {

    MediaTable table;

    @BeforeEach
    public void setUp() throws Exception {

        table = new MediaTable();

        EasyMedia media=new AudioVideoMedia(new File("src/test/resources/Alarme.wav"));
        ((AudioVideoMedia)media).setDuration(new Duration(2000));
        ((AudioVideoMedia)media).setVolume(20L);
        ((AudioVideoMedia)media).setFadeInDuration(new Duration(1200));
        ((AudioVideoMedia)media).setFadeOutDuration(Duration.UNKNOWN);
        table.getEasyMediaList().add(media);

    }

    @Test
    public void testSerialization()  {

        EasyMedia media=new AudioVideoMedia(new File("src/test/resources/Alarme.wav"));
        ((AudioVideoMedia)media).setDuration(new Duration(2000));
        ((AudioVideoMedia)media).setVolume(20L);
        ((AudioVideoMedia)media).setFadeInDuration(new Duration(1200));
        ((AudioVideoMedia)media).setFadeOutDuration(Duration.INDEFINITE);


        XStream xstream = new XStream();

// allow some basics
        xstream.addPermission(NullPermission.NULL);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(Collection.class);
// allow any type from the same package
        xstream.allowTypesByWildcard(new String[] {
                Easyconduite.class.getPackage().getName()+".*",AudioVideoMedia.class.getPackage().getName()+".*"
        });


        String toto =xstream.toXML(media);

        EasyMedia media2 = (EasyMedia) xstream.fromXML(toto);
        assertEquals(Duration.INDEFINITE,((AudioVideoMedia)media2).getFadeOutDuration());
        System.out.println(media.getUniqueId());
        System.out.println(media2.getUniqueId());

    }
}
