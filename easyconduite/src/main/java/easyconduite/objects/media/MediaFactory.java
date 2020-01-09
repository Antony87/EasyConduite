package easyconduite.objects.media;

import easyconduite.model.IeasyMedia;

import java.io.File;

/**
 * Cette classe implémente une Factory retournant un {@link IeasyMedia}.
 */
public class MediaFactory {

    public static IeasyMedia getAudioVisualMedia(File file) {
        IeasyMedia media = new AudioVideoMedia(file);
        return media;
    }
}
