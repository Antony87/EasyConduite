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

import com.jfoenix.controls.JFXSlider;
import easyconduite.controllers.MainController;
import easyconduite.media.RemoteMedia;
import easyconduite.model.AbstractUIMedia;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * RemoteMedia (Kodi or VLC).
 *
 * @author A Fons
 * @since 1.0
 */
public class RemoteMediaUI extends AbstractUIMedia {

    static final Logger LOG = LogManager.getLogger(RemoteMediaUI.class);
    private final RemoteMedia remoteMedia;
    private final Tooltip typeRegionToolTip = new Tooltip();

    /**
     * Constructor du UI custom control for an AudioMedia.
     *
     * @param media      a media wich be play.
     * @param controller the main controller which interact with {@link RemoteMediaUI}
     */
    public RemoteMediaUI(RemoteMedia media, MainController controller) {
        super(media, controller);
        LOG.info("Construct an AudioMedia {}", media);
        this.remoteMedia = media;

        remoteMedia.statusProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                LOG.trace("MediaStatus player {} is {}", this.remoteMedia.getName(), newValue);
                switch (newValue) {
                    case PAUSED:
                        playingClass.setValue(false);
                        break;
                    case PLAYING:
                        playingClass.setValue(true);
                        break;
                    case READY:
                        playingClass.setValue(false);
                        break;
                    case STOPPED:
                        playingClass.setValue(false);
                        break;
                    default:
                        break;
                }
            }
        });


        Tooltip.install(typeRegion,typeRegionToolTip);

        if (remoteMedia.getType().equals(RemoteMedia.Type.KODI)) {
            super.typeRegion.getStyleClass().add("typeKodi");
        }

        contextHbox.getChildren().add(new JFXVolumeSlider());

        ActiveHostObserver hostObserver = new ActiveHostObserver();
        hostObserver.setPeriod(Duration.millis(1000));
        hostObserver.start();

    }

    @Override
    public void playPause() {
        remoteMedia.play();
    }

    @Override
    public void stop() {
        remoteMedia.stop();
    }

    private class JFXVolumeSlider extends JFXSlider {
        private boolean changing;

        protected JFXVolumeSlider() {
            super(0, 100, remoteMedia.getVolume() * 100);
            final DoubleProperty volumeProperty = RemoteMediaUI.JFXVolumeSlider.this.valueProperty();
        }
    }

    @Override
    public void actualizeUI() {
        super.actualizeUI();
        typeRegionToolTip.setText(remoteMedia.getHost());
    }

    private class ActiveHostObserver extends ScheduledService<Void>{

        @Override
        protected Task<Void> createTask() {

            return new Task<Void>() {
                @Override
                protected Void call() {
                    boolean active = remoteMedia.isActiveHost();
                    boolean kodiactive = typeRegion.getStyleClass().contains("typeKodi");
                    boolean kodiactiveKo = typeRegion.getStyleClass().contains("typeKodiKo");
                    if(!active && kodiactive){
                        typeRegion.getStyleClass().remove("typeKodi");
                        typeRegion.getStyleClass().add("typeKodiKo");
                        if(!playPauseHbox.isDisable()) playPauseHbox.setDisable(true);
                    }else if(active && kodiactiveKo){
                        typeRegion.getStyleClass().remove("typeKodiKo");
                        typeRegion.getStyleClass().add("typeKodi");
                        if(playPauseHbox.isDisable()) playPauseHbox.setDisable(false);
                    }
                    return null;
                }
            };
        }
    }

}
