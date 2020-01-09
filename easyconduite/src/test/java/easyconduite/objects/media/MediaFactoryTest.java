package easyconduite.objects.media;

import easyconduite.model.IeasyMedia;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MediaFactoryTest {

    @Test
    public void getEasyMedia() {

        IeasyMedia soundMedia = MediaFactory.getAudioVisualMedia(new File(""));
        assertNotNull(soundMedia);
        assertTrue(soundMedia instanceof AudioVideoMedia);

    }

}