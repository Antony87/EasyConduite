package easyconduite.objects.media;

import easyconduite.model.IeasyMedia;

public class MediaFactory {

    public static IeasyMedia getEasyMedia() {

        IeasyMedia media = null;

        media = new SoundMedia(null);

        return media;
    }
}
