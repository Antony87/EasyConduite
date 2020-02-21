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
 *
 * @author Antony Fons
 * @since 3.0
 */
public abstract class AbstractUIMedia extends VBox implements UIResourcePlayable {

    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);
    protected final Label timeLabel = new Label();
    protected final Label keycodeLabel = new Label();
    protected final HBox contextHbox = new HBox();
    protected final HBox playPauseHbox = new HBox();
    private final VBox mediaVboxNode = new VBox();
    protected final BooleanProperty playingClass = new PlayingPseudoClass(mediaVboxNode);
    protected final BooleanProperty mediaSelectedClass = new MediaSelectedPseudoClass(mediaVboxNode);
    private final Label nameLabel = new Label();
    private final Region repeatRegion = new Region();
    private final EasyMedia easyMedia;
    protected ResourceBundle locale;

    /**
     * Constructeur.
     *
     * @param media      the media displayed
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
        // Gestion des mouse events pour la sélection de l'UI.
        mediaVboxNode.setOnMouseClicked(eventFocus -> {
            if (eventFocus.getButton().equals(MouseButton.PRIMARY)) {
                mediaVboxNode.requestFocus();
                if (mediaVboxNode.isFocused()) {
                    controller.getMediaUIList().forEach(mediaUI -> setSelected(false));
                    setSelected(true);
                }
            }
            if (eventFocus.getClickCount() == 2) {
                controller.editTrack(this);
            }
            eventFocus.consume();
        });

        nameLabel.setMouseTransparent(true);

        // Construction de la partie contextuelle, centrale de l'UI.
        // sans le slider de volume qui est spécifique.
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

        playPauseHbox.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                Region target = (Region) mouseEvent.getTarget();
                if (target.getStyleClass().contains("stopbutton")) {
                    stop();
                } else {
                    playPause();
                }
            }
            mouseEvent.consume();
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


    @Override
    public boolean isSelected() {
        return mediaSelectedClass.getValue();
    }

    @Override
    public void setSelected(boolean selected) {
        mediaSelectedClass.setValue(selected);
    }

    public VBox getMediaVboxNode() {
        return mediaVboxNode;
    }

    @Override
    public EasyMedia getEasyMedia() {
        return easyMedia;
    }

    @Override
    public abstract void playPause();

    @Override
    public abstract void stop();
}
