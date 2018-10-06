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
package easyconduite.ui;

import easyconduite.controllers.EasyconduiteController;
import easyconduite.exception.EasyconduiteException;
import easyconduite.model.EasyAudioChain;
import easyconduite.objects.AudioMedia;
import easyconduite.ui.commons.ActionDialog;
import easyconduite.ui.controls.EasyconduitePlayer;
import easyconduite.util.Constants;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.KeyCodeUtil;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * AudioMedia.
 *
 * @author A Fons
 */
public class AudioMediaUI extends VBox implements EasyAudioChain {
    
    static final Logger LOG = LogManager.getLogger(AudioMediaUI.class);
    
    private final SimpleDateFormat PROGRESS_TIME_FORMAT = new SimpleDateFormat("mm:ss:SSS");
    
    private final ObjectProperty<KeyCode> keycode = new ReadOnlyObjectWrapper<>();
    
    private final Label nameLabel = new Label();
    
    private final Label timeLabel = new Label();
    
    private final PlayPauseHbox playPauseHbox;
    
    private final Label keycodeLabel = new Label();
    
    private final Slider sliderVolume = new Slider(0, 1, 0.5d);
    
    private final HBox keycodeHbox = new HBox();
    
    private final ContextMenu contextMenu = new ContextMenu();
    
    private final ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
    
    private EasyconduitePlayer player;
    
    private AudioMedia audioMedia;
    
    private EasyAudioChain nextChain;

    /**
     * Constructor du UI custom control for an AudioMedia.<br>
     * Not draw the control but construct object and assign a
     * {@link MediaPlayer}.<br>
     *
     * @param media
     * @param controller
     */
    public AudioMediaUI(AudioMedia media, EasyconduiteController controller) {
        super();
        LOG.info("Construct an AudioMedia {}", media);

        // test sur drag&drop
        this.setOnDragDetected((mouseEvent) -> {
            
            final Dragboard dragBroard = this.startDragAndDrop(TransferMode.COPY);
            // Remlissage du contenu. 
            ClipboardContent content = new ClipboardContent();
            // recup de l'index children
            final Integer index = controller.getTableLayout().getChildren().indexOf(this);
            content.put(Constants.DATA_FORMAT_INTEGER, index);
            dragBroard.setContent(content);
            mouseEvent.consume();
        });

        this.setOnDragOver((dragEvent) -> {
            final Dragboard dragBroard = dragEvent.getDragboard();
            
            if (dragEvent.getGestureSource() != this && dragBroard.hasContent(Constants.DATA_FORMAT_INTEGER)) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            }
            dragEvent.consume();
        });
        
        this.setOnDragDropped((dragEvent) -> {
            
            final Dragboard dragBroard = dragEvent.getDragboard();
            final ObservableList<Node> tableChildren = controller.getTableLayout().getChildren();
            final Integer idSource = (Integer) dragBroard.getContent(Constants.DATA_FORMAT_INTEGER);
            final AudioMediaUI sourceUi = (AudioMediaUI) tableChildren.get(idSource);
            tableChildren.set(idSource, new VBox());
            tableChildren.set(tableChildren.indexOf(this), sourceUi);
            tableChildren.set(idSource, this);
            
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
        });
        
        this.setOnDragDone(dragEvent -> {
            if (dragEvent.getTransferMode() == TransferMode.COPY) {
                dragEvent.getDragboard().clear();
            }
            dragEvent.consume();
        });
        
        PROGRESS_TIME_FORMAT.setTimeZone(Constants.TZ);
        
        this.audioMedia = media;
        ////////////////////////////////////////////////////////////////////////
        //               Initialize MediaPlayer
        ////////////////////////////////////////////////////////////////////////
        player = null;
        try {
            player = EasyconduitePlayer.create(media);
            // Listenning player Status property
            player.getPlayer().statusProperty().addListener(getPlayerStatusListener());
        } catch (EasyconduiteException ex) {
            LOG.error("Error occurend during EasyPlayer construction", ex);
        }
        // Positionne la chaine de responsabilité
        this.nextChain = player;

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

        ////////////////////////////////////////////////////////////////////////
        // Slider for volume control
        sliderVolume.setValue(audioMedia.getVolume());
        //sliderVolume.getStyleClass().add("slider-volume-track");
        sliderVolume.valueProperty().bindBidirectional(player.getPlayer().volumeProperty());
        sliderVolume.setOnMouseReleased((MouseEvent event) -> {
            if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                audioMedia.setVolume(sliderVolume.getValue());
                // propagation au EasyconduitePlayer.
                player.updateFromAudioMedia(media);
            }
        });
        ////////////////////////////////////////////////////////////////////////
        ///////////// current Time label               
        //timeLabel.getStyleClass().add("texte-time");
        player.getPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            timeLabel.setText(Constants.getFormatedDuration(audioMedia.getAudioDuration().subtract(newValue), PROGRESS_TIME_FORMAT));
        });
        keycodeHbox.getStyleClass().add("baseHbox");
        // ToolBar with Stop and play button ///////////////////////////////////
        playPauseHbox = new PlayPauseHbox(player);
        ////////////////////////////////////////////////////////////////////////
        // initialize KeyCodeLabel
        keycodeHbox.getChildren().add(keycodeLabel);
        // All childs are built. Call updateFromAudio to set there.
        this.updateFromAudioMedia(audioMedia);
        
        MenuItem propertiesItem = new MenuItem(bundle.getString("track.context.properties"));
        propertiesItem.setOnAction((ActionEvent e) -> {
            controller.editTrack(this);
            e.consume();
        });
        
        MenuItem deleteItem = new MenuItem(bundle.getString("track.context.delete"));
        deleteItem.setOnAction((ActionEvent e) -> {
            controller.deleteTrack(this);
            e.consume();
        });
        
        MenuItem hideItem = new MenuItem(bundle.getString("dialog.button.cancel"));
        hideItem.setOnAction((ActionEvent e) -> {
            contextMenu.hide();
            e.consume();
        });
        
        contextMenu.getItems().addAll(propertiesItem, deleteItem, hideItem);
        
        this.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, contextMenuEvent -> {
            contextMenu.show(this, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            contextMenuEvent.consume();
        });
        
        this.getChildren().addAll(nameLabel, sliderVolume, timeLabel, keycodeHbox, playPauseHbox);
    }
    
    public final AudioMedia getAudioMedia() {
        return audioMedia;
    }
    
    public final EasyconduitePlayer getEasyPlayer() {
        return player;
    }
    
    public ObjectProperty<KeyCode> keycodeProperty() {
        return keycode;
    }
    
    public KeyCode getKeycode() {
        return keycode.get();
    }
    
    @Override
    public void setNext(EasyAudioChain next) {
        nextChain = next;
    }
    
    @Override
    public void updateFromAudioMedia(AudioMedia media) {
        if (audioMedia.equals(media)) {
            // positionne tous les champs quand audioMedia a changé et que la 
            // "chain of responsability" est déclenchée.
            nameLabel.setText(audioMedia.getName());
            timeLabel.setText(Constants.getFormatedDuration(audioMedia.getAudioDuration(), PROGRESS_TIME_FORMAT));
            keycodeLabel.setText(KeyCodeUtil.toString(this.audioMedia.getKeycode()));
            keycode.setValue(this.audioMedia.getKeycode());
            // on passe la responsabilité au next (EasyconduitePlayer).
            nextChain.updateFromAudioMedia(this.audioMedia);
        } else {
            ActionDialog.showWarning("Incohérence des objets", "Les objets AudioMedia ne sont pas egaux");
        }
    }
    
    @Override
    public void removeChilds(AudioMedia audioMedia) {
        nextChain.removeChilds(audioMedia);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Listener
    ////////////////////////////////////////////////////////////////////////////
    private ChangeListener<Status> getPlayerStatusListener() {
        return (ObservableValue<? extends Status> observable, Status oldValue, Status newValue) -> {
            switch (newValue) {
                case PAUSED:
                    playPauseHbox.decoratePlaying(false);
                    break;
                case PLAYING:
                    playPauseHbox.decoratePlaying(true);
                    break;
                case STOPPED:
                    playPauseHbox.decoratePlaying(false);
                    break;
                case READY:
                    // if player ready, update AudioMedia duration an UI.
                    audioMedia.setAudioDuration(player.getPlayer().getStopTime());
                    updateFromAudioMedia(AudioMediaUI.this.audioMedia);
                    break;
            }
        };
    }
    
    private class PlayPauseHbox extends HBox {
        
        final Region stopRegion;
        final Region playRegion;
        
        protected PlayPauseHbox(EasyconduitePlayer player) {
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
                    player.playPause();
                    event.consume();
                }
            });
            
            PlayPauseHbox.this.getChildren().addAll(stopRegion, playRegion);
        }
        
        protected void decoratePlaying(boolean decorate) {
            playRegion.getStyleClass().remove(0);
            if (decorate) {
                playRegion.getStyleClass().add("pausebutton");
            } else {
                playRegion.getStyleClass().add("playbutton");
            }
        }
    }
}
