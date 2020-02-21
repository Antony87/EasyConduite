package easyconduite.model;

import easyconduite.exception.EasyconduiteException;

public interface ResourcePlayable {

    /**
     * Méthode jouant un EasyMédia.
     */
    void play();

    /**
     * Méthode mettant en pause un EasyMédia
     */
    void pause();

    /**
     * Méthode stoppant un EasyMédia
     */
    void stop();

    /**
     * Méthode initialisant le player avant utilisation dans l'application.
     */
    void initPlayer() throws EasyconduiteException;

    void closePlayer();

}
