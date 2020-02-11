package easyconduite.objects.media;

import easyconduite.model.EasyMedia;
import easyconduite.model.IEasyMedia;

import java.io.File;

/**
 * Cette classe implémente une Factory retournant un {@link IEasyMedia}.
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
