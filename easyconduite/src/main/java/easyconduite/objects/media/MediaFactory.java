package easyconduite.objects.media;

import easyconduite.exception.EasyconduiteException;
import easyconduite.model.EasyMedia;
import easyconduite.model.IeasyMedia;

import java.io.File;
import java.nio.file.Files;

/**
 * Cette classe implémente une Factory retournant un {@link IeasyMedia}.
 */
public class MediaFactory {

    public static <T> EasyMedia getAudioVisualMedia(T param) {
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
