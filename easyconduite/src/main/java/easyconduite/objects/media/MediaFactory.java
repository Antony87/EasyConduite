package easyconduite.objects.media;

import easyconduite.model.EasyMedia;
import easyconduite.model.ResourcePlayable;

import java.io.File;

/**
 * Cette classe implémente une Factory retournant un {@link ResourcePlayable}.
 */
public class MediaFactory {

    public static <T> EasyMedia getPlayableMedia(T param) {
        EasyMedia media=null;
        if(param instanceof File){
            File mediaFile = (File)param;
            if(mediaFile.exists()){
                return new AudioMedia(mediaFile.toURI());
            }
        }
    return null;
    }
}
