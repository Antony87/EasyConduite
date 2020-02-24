package easyconduite.objects.media;

import easyconduite.model.EasyMedia;
import easyconduite.model.ResourcePlayable;

import java.io.File;

/**
 * This class is a factory of EasyMedia.
 * @see EasyMedia
 * @see ResourcePlayable
 *
 * @since 3.0
 *
 */
public class MediaFactory {

    /**
     * This method create a concrete playable media of EasyMedia.
     * <p>
     *     The type of {@link ResourcePlayable} is determine by the param :
     *     <ul>
     *         <li>Param = {@link File} then create {@link AudioMedia}</li>
     *         <li>Param = {@link RemotePlayer.Type} then create {@link RemotePlayer}</li>
     *     </ul>
     * </p>
     * @param param The param which is used to determine concrete type of EasyMedia
     * @param <T>
     * @return an {@link ResourcePlayable} media
     */
    public static <T> EasyMedia getPlayableMedia(T param) {
        EasyMedia media=null;
        if(param instanceof File){
            File mediaFile = (File)param;
            if(mediaFile.exists()){
                return new AudioMedia(mediaFile.toURI());
            }
        }else if(param.equals(RemotePlayer.Type.KODI)){
                return new RemotePlayer((RemotePlayer.Type) param);
        }
    return null;
    }
}
