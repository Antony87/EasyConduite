package easyconduite.model;

import easyconduite.exception.EasyconduiteException;
import javafx.scene.input.KeyCode;

public interface MediaPlayable {

    /**
     * This method triggers the reading of a media.
     */
    void play();

    /**
     * Method pausing a Media.
     */
    void pause();

    /**
     * This method stops a Media.
     */
    void stop();

    /**
     * Method initializing Media.
     * <p>This is mandatory before using the Media in the application</p>
     */
    void initPlayer() throws EasyconduiteException;

    /**
     * Method closing a Media.
     * <p>For example, call dispose method for {@link javafx.scene.media.MediaPlayer}</p>
     */
    void closePlayer();

    KeyCode getKeycode();

    boolean getLoppable();

    String getName();



}
