package easyconduite.objects.media;

import easyconduite.model.AbstractMedia;
import easyconduite.model.MediaPlayable;

import java.io.File;

/**
 * This class is a factory of AbstractMedia.
 * @see AbstractMedia
 * @see MediaPlayable
 *
 * @since 3.0
 *
 */
public class MediaFactory {

    /**
     * This method create a concrete playable media of AbstractMedia.
     * <p>
     *     The type of {@link MediaPlayable} is determine by the param :
     *     <ul>
     *         <li>Param = {@link File} then create {@link AudioMedia}</li>
     *         <li>Param = {@link RemotePlayer.Type} then create {@link RemotePlayer}</li>
     *     </ul>
     * </p>
     * @param param The param which is used to determine concrete type of AbstractMedia
     * @param <T>
     * @return an {@link MediaPlayable} media
     */
    public static <T> AbstractMedia createPlayableMedia(T param) {
        AbstractMedia media=null;
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
