package easyconduite.model;

import easyconduite.controllers.MainController;
import easyconduite.exception.EasyconduiteException;
import easyconduite.project.ProjectContext;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.KeyCodeHelper;
import easyconduite.view.commons.MediaSelectedPseudoClass;
import easyconduite.view.commons.PlayingPseudoClass;
import easyconduite.view.controls.ActionDialog;
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
public abstract class AbstractUIMedia extends VBox implements UIMediaPlayable {

    static final Logger LOG = LogManager.getLogger(AbstractUIMedia.class);
    private static final MainController controller = ProjectContext.getContext().getMainControler();
    /**
     * message that appears on the dialog box header.
     */
    private static final String DIALOG_EXCEPTION_HEADER = "dialog.exception.header";

    private static final String REPEAT_CSS = "repeat";

    protected final Label timeLabel = new Label();

    protected final Label keycodeLabel = new Label();

    protected final HBox contextHbox = new HBox();

    protected final HBox playPauseHbox = new HBox();

    protected final Region typeRegion = new Region();

    protected final BooleanProperty playingClass = new PlayingPseudoClass(this);

    protected final BooleanProperty mediaSelectedClass = new MediaSelectedPseudoClass(this);

    private final Label nameLabel = new Label();

    private final Region repeatRegion = new Region();

    private final AbstractMedia abstractMedia;

    protected ResourceBundle locale;

    /**
     * Constructeur.
     *
     * @param media the media displayed
     */
    public AbstractUIMedia(AbstractMedia media) {
        super();
        LOG.trace("Construct AbstractUIMedia with {}", media);
        this.abstractMedia = media;
        try {
            locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        } catch (EasyconduiteException e) {
            ActionDialog.showException(locale.getString(DIALOG_EXCEPTION_HEADER), locale.getString("task.create.error"), e);
            LOG.error("Error occured within constructor {}", this, e);
        }
        ////////////////////////////////////////////////////////////////////////
        //                 Construction de l'UI
        ////////////////////////////////////////////////////////////////////////
        // attribution css for Track VBOX
        this.getStyleClass().add("audioMediaUi");
        // Gestion des mouse events pour la sélection de l'UI.
        this.setOnMouseClicked(eventFocus -> {
            if (eventFocus.getButton().equals(MouseButton.PRIMARY)) {
                this.requestFocus();
                if (this.isFocused()) {
                    controller.getMediaUIList().forEach(mediaUI -> mediaUI.setSelected(false));
                    this.setSelected(true);
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
        typeRegion.setId("typeRegion");

        repeatRegion.setId("repeatRegion");
        if (abstractMedia.getLoppable()) {
            repeatRegion.getStyleClass().add(REPEAT_CSS);
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
        this.getChildren().addAll(nameLabel, contextHbox, timeLabel, keycodeLabel, playPauseHbox);

    }

    @Override
    public void actualizeUI() {
        nameLabel.setText(abstractMedia.getName());
        timeLabel.setText(formatTime(abstractMedia.getDuration()));
        keycodeLabel.setText(KeyCodeHelper.toString(this.abstractMedia.getKeycode()));
        repeatRegion.getStyleClass().remove(REPEAT_CSS);
        if (abstractMedia.getLoppable()) {
            repeatRegion.getStyleClass().add(REPEAT_CSS);
        }
    }

    /**
     * This is the time string formater for display human-readable the media duration.
     *
     * @param duration a duration
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

    @Override
    public AbstractMedia getMediaPlayable() {
        return abstractMedia;
    }

}
