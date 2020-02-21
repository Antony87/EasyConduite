package easyconduite.model;

import easyconduite.controllers.MainController;
import easyconduite.exception.EasyconduiteException;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.KeyCodeHelper;
import easyconduite.view.AudioMediaUI;
import easyconduite.view.commons.MediaSelectedPseudoClass;
import easyconduite.view.commons.PlayingPseudoClass;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

/**
 * This class implements the common behaviors of the Media UI.
 */
public abstract class AbstractUIMedia extends VBox implements UIResourcePlayable{

    private final VBox mediaVboxNode= new VBox();

    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);
    private final Label nameLabel = new Label();
    protected final Label timeLabel = new Label();
    protected final Label keycodeLabel = new Label();
    private final Region repeatRegion = new Region();
    protected final HBox contextHbox = new HBox();
    protected final HBox playPauseHbox = new HBox();
    private final EasyMedia easyMedia;
    protected final BooleanProperty playingClass = new PlayingPseudoClass(this);
    protected final BooleanProperty mediaSelectedClass = new MediaSelectedPseudoClass(this);
    protected ResourceBundle locale;

    /**
     * Constructeur.
     * @param media the media displayed
     * @param controller the main controller
     */
    public AbstractUIMedia(EasyMedia media, MainController controller) {
        this.easyMedia = media;
        try {
            locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        } catch (EasyconduiteException e) {
            //TODO
            e.printStackTrace();
        }
        ////////////////////////////////////////////////////////////////////////
        //                 Construction de l'UI
        ////////////////////////////////////////////////////////////////////////
        // attribution css for Track VBOX
        mediaVboxNode.getStyleClass().add("audioMediaUi");
        // Name of the track, just top of the VBOX (this).
        nameLabel.setMouseTransparent(true);

        // Construction de la partie contextuelle
        //TODO extraire dans une classe et créer une classe abstraite MediaUI.
        contextHbox.setId("contextHbox");
        final VBox infoVbox = new VBox();
        infoVbox.setId("infoVbox");
        final Region typeRegion = new Region();
        typeRegion.setId("typeRegion");
        typeRegion.getStyleClass().add("typeAudio");
        repeatRegion.setId("repeatRegion");
        if (easyMedia.getLoppable()) {
            repeatRegion.getStyleClass().add("repeat");
        }
        infoVbox.getChildren().addAll(typeRegion, repeatRegion);

        playPauseHbox.getStyleClass().add("commandHbox");

        final Region playRegion = new Region();
        final Region stopRegion = new Region();
        stopRegion.getStyleClass().add("stopbutton");
        playRegion.getStyleClass().add("playbutton");
        playPauseHbox.getChildren().addAll(stopRegion, playRegion);

        // Gestion des mouse events pour la sélection d'un UI.
        mediaVboxNode.setOnMouseClicked(eventFocus -> {
            if (eventFocus.getButton().equals(MouseButton.PRIMARY)) {
                mediaVboxNode.requestFocus();
                if (mediaVboxNode.isFocused()) {
                    controller.getMediaUIList().forEach(mediaUI -> setSelected(false));
                    setSelected(true);
                }
            }
            if (eventFocus.getClickCount() == 2) {
                controller.editTrack((UIResourcePlayable) this);
            }
            eventFocus.consume();
        });

        contextHbox.getChildren().add(infoVbox);
        mediaVboxNode.getChildren().addAll(nameLabel, contextHbox, timeLabel, keycodeLabel, playPauseHbox);

    }

    public void actualizeUI() {
        nameLabel.setText(easyMedia.getName());
        timeLabel.setText(formatTime(easyMedia.getDuration()));
        keycodeLabel.setText(KeyCodeHelper.toString(this.easyMedia.getKeycode()));
        repeatRegion.getStyleClass().remove("repeat");
        if (easyMedia.getLoppable()) {
            repeatRegion.getStyleClass().add("repeat");
        }
    }

    /**
     * This is the time string formater for display human-readable the media duration.
     *
     * @param duration
     * @return the time duration in mm:ss:dc format (dc = 1/10 s)
     */
    protected String formatTime(Duration duration) {

        if (duration.greaterThan(Duration.ZERO)) {
            final double millis = duration.toMillis();
            final int dec = (int) ((millis / 100) % 10);
            final int seconds = (int) ((millis / 1000) % 60);
            final int minutes = (int) (millis / (1000 * 60));
            return String.format("%02d:%02d:%02d", minutes, seconds, dec);
        }
        return null;
    }


    public boolean isSelected() {
        return mediaSelectedClass.getValue();
    }

    public void setSelected(boolean selected) {
        mediaSelectedClass.setValue(selected);
    }

    public VBox getMediaVboxNode() {
        return mediaVboxNode;
    }

    public EasyMedia getEasyMedia() {
        return easyMedia;
    }
}
