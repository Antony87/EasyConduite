package easyconduite.objects.media;

import easyconduite.model.EasyMedia;
import easyconduite.model.IeasyMedia;

import java.io.File;

/**
 * Cette classe implémente une Factory retournant un {@link IeasyMedia}.
 */
public class MediaFactory {

    public static <T> EasyMedia getAudioVideoMedia(T param) {
        EasyMedia media=null;
        if(param instanceof File){
            File mediaFile = (File)param;
            if(mediaFile.exists()){
                return new AudioVideoMedia(mediaFile);
            }
        }
    return null;
    }
}
