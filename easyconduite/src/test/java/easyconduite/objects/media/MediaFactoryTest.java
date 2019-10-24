package easyconduite.objects.media;

import easyconduite.model.IeasyMedia;
import org.junit.Test;

import static org.junit.Assert.*;

public class MediaFactoryTest {

    @Test
    public void getEasyMedia() {

        IeasyMedia soundMedia = MediaFactory.getEasyMedia();
        assertNotNull(soundMedia);
        assertTrue(soundMedia instanceof SoundMedia);

    }
}