/*
 * Copyright (C) 2017 Antony Fons
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package easyconduite.view;

import easyconduite.controllers.MainController;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.view.controls.EasyconduitePlayer;
import easyconduite.exception.EasyconduiteException;
import easyconduite.objects.AudioMedia;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.util.KeyCodeHelper;
import easyconduite.view.controls.ActionDialog;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import easyconduite.model.ChainingUpdater;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI extends VBox {

    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);

    private final Label nameLabel = new Label();

    private final Label timeLabel = new Label();

    private final Label keycodeLabel = new Label();

    private final HBox keycodeHbox = new HBox();
    
    private final Region repeatRegion = new Region();

    private final ContextMenu contextMenu = new ContextMenu();

    private final PlayPauseHbox playPauseHbox;

    private AudioVideoMedia audioMedia;

    //private ChainingUpdater nextChain;

    /**
     * Constructor du UI custom control for an AudioMedia.<br>
     * Not draw the control but construct object and assign a
     * {@link MediaPlayer}.<br>
     *
     * @param media
     */
    public AudioMediaUI(AudioVideoMedia media) {
        super();
        LOG.info("Construct an AudioMedia {}", media);

        this.audioMedia = media;
        ////////////////////////////////////////////////////////////////////////
        //               Initialize MediaPlayer
        ////////////////////////////////////////////////////////////////////////

        //player = null;
        try {
            audioMedia.initPlayer();
            //player = EasyconduitePlayer.create(media,controller);
            // Listenning player Status property
            audioMedia.getPlayer().statusProperty().addListener(getPlayerStatusListener());
        } catch (EasyconduiteException ex) {
            LOG.error("Error occurend during EasyPlayer construction", ex);
        }
        // Positionne la chaine de responsabilité
        //this.nextChain = player;

        ////////////////////////////////////////////////////////////////////////
        //                 Construction de l'UI
        ////////////////////////////////////////////////////////////////////////
        // attribution css for Track VBOX
        this.getStyleClass().add("audioMediaUi");

        this.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                this.requestFocus();
            }
        });
        //this.updateFromAudioMedia(audioMedia);

        ////////////////////////////////////////////////////////////////////////
        ///////////// current Time label               
        audioMedia.getPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            timeLabel.setText(formatTime(audioMedia.getDuration().subtract(newValue)));
        });

        ////////////////////////////////////////////////////////////////////////
        // initialize KeyCodeLabel
        keycodeHbox.getStyleClass().add("baseHbox");
    
        repeatRegion.setId("repeatRegion");
        if(audioMedia.getLoppable()){
            repeatRegion.getStyleClass().add("repeat");
        }
        keycodeHbox.getChildren().addAll(repeatRegion,keycodeLabel);


        ResourceBundle bundle = null;
        try {
            bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }

//        MenuItem propertiesItem = new MenuItem(bundle.getString("track.context.properties"));
//        propertiesItem.setOnAction((ActionEvent e) -> {
//            player.stop();
//            controller.editTrack(this);
//            e.consume();
//        });
//
//        MenuItem deleteItem = new MenuItem(bundle.getString("track.context.delete"));
//        deleteItem.setOnAction((ActionEvent e) -> {
//            controller.deleteTrack(this);
//            e.consume();
//        });
//
//        contextMenu.getItems().addAll(propertiesItem, deleteItem);
//
//        this.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, contextMenuEvent -> {
//            contextMenu.setAutoHide(true);
//            contextMenu.show(this, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
//            contextMenuEvent.consume();
//        });
        playPauseHbox = new PlayPauseHbox(media.getPlayer());

        timeLabel.setMouseTransparent(true);
        keycodeLabel.setMouseTransparent(true);
        keycodeHbox.setMouseTransparent(true);
        nameLabel.setMouseTransparent(true);

        this.getChildren().addAll(nameLabel, new AudioSlider(), timeLabel, keycodeHbox, playPauseHbox);
    }

    //FIXME
    @Deprecated
    public final AudioMedia getAudioMedia() {
        return new AudioMedia(null);
    }

//    public final EasyconduitePlayer getEasyPlayer() {
//        return player;
//    }

//    @Override
//    public void setNext(ChainingUpdater next) {
//        nextChain = next;
//    }
//
//    @Override
//    public void updateFromAudioMedia(AudioMedia media) {
//        if (audioMedia.equals(media)) {
//            // positionne tous les champs quand audioMedia a changé et que la
//            // "chain of responsability" est déclenchée.
//            nameLabel.setText(audioMedia.getName());
//            timeLabel.setText(formatTime(audioMedia.getDuration()));
//            keycodeLabel.setText(KeyCodeHelper.toString(this.audioMedia.getKeycode()));
//            repeatRegion.getStyleClass().remove("repeat");
//            if(audioMedia.getRepeatable()){
//                repeatRegion.getStyleClass().add("repeat");
//            }
//            // on passe la responsabilité au next (EasyconduitePlayer).
//            nextChain.updateFromAudioMedia(this.audioMedia);
//        } else {
//            ActionDialog.showWarning("Incohérence des objets", "Les objets AudioMedia ne sont pas egaux");
//        }
//    }
//
//    @Override
//    public void removeChild(ChainingUpdater audioMedia) {
//        nextChain.removeChild(audioMedia);
//    }

    private String formatTime(Duration duration) {

        if (duration.greaterThan(Duration.ZERO)) {
            final double millis = duration.toMillis();
            final int dec = (int) ((millis / 100) % 10);
            final int seconds = (int) ((millis / 1000) % 60);
            final int minutes = (int) (millis / (1000 * 60));
            return String.format("%02d:%02d:%02d", minutes, seconds, dec);
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Listener
    ////////////////////////////////////////////////////////////////////////////
    private ChangeListener<Status> getPlayerStatusListener() {
        return (ObservableValue<? extends Status> observable, Status oldValue, Status newValue) -> {
            switch (newValue) {
                case PAUSED:
                    playPauseHbox.decoratePlaying(false);
                    decorateAudioPlaying(false);
                    break;
                case PLAYING:
                    playPauseHbox.decoratePlaying(true);
                    decorateAudioPlaying(true);
                    break;
                case STOPPED:
                    playPauseHbox.decoratePlaying(false);
                    decorateAudioPlaying(false);
                    break;
                case READY:
                    // if player ready, update AudioMedia duration an UI.
                    audioMedia.setDuration(audioMedia.getPlayer().getStopTime());
                    //updateFromAudioMedia(AudioMediaUI.this.audioMedia);
                    break;
            }
        };
    }

    private class AudioSlider extends Slider {

        protected AudioSlider() {
            super(0, 1, 0.5d);
            AudioSlider.this.setValue(audioMedia.getVolume());
            //sliderVolume.getStyleClass().add("slider-volume-track");
            AudioSlider.this.valueProperty().bindBidirectional(audioMedia.getPlayer().volumeProperty());
            AudioSlider.this.setOnMouseReleased((MouseEvent event) -> {
                if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                    audioMedia.setVolume(AudioSlider.this.getValue());
                    audioMedia.getPlayer().setVolume(AudioSlider.this.getValue());
                    // propagation au EasyconduitePlayer.
                    //player.updateFromAudioMedia(audioMedia);
                }
            });
        }
    }

    private void decorateAudioPlaying(boolean playing) {
        this.getStyleClass().parallelStream().filter((t) -> {
            return t.equals("playing");
        }).forEach((t) -> {
            this.getStyleClass().remove(t);
        });
        if (playing) {
            this.getStyleClass().add("playing");
        }
    }

    private final void playPause() {
        Status status = audioMedia.getPlayer().getStatus();
        switch (status) {
            case PAUSED:
                audioMedia.play();
                break;
            case PLAYING:
                audioMedia.pause();
                break;
            case READY:
                audioMedia.play();
                break;
            case STOPPED:
                audioMedia.play();
                break;
            default:
                break;
        }
    }

    private class PlayPauseHbox extends HBox {

        final Region stopRegion;
        final Region playRegion;

        protected PlayPauseHbox(MediaPlayer player) {
            super();
            PlayPauseHbox.this.getStyleClass().add("commandHbox");
            stopRegion = new Region();
            stopRegion.getStyleClass().add("stopbutton");
            stopRegion.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    player.stop();
                    event.consume();
                }
            });

            playRegion = new Region();
            playRegion.getStyleClass().add("playbutton");
            playRegion.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    AudioMediaUI.this.playPause();
                    event.consume();
                }
            });

            PlayPauseHbox.this.getChildren().addAll(stopRegion, playRegion);
        }

        protected void decoratePlaying(boolean playing) {
            playRegion.getStyleClass().remove(0);
            if (playing) {
                playRegion.getStyleClass().add("pausebutton");
            } else {
                playRegion.getStyleClass().add("playbutton");
            }
        }
    }
}
