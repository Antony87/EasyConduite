package easyconduite.media;

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
     *         <li>Param = {@link RemoteMedia.RemoteType} then create {@link RemoteMedia}</li>
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
                return new AudioMedia(mediaFile.toPath());
            }
        }else if(param.equals(RemoteMedia.RemoteType.KODI)){
                return new RemoteMedia((RemoteMedia.RemoteType) param);
        }
    return null;
    }
}
