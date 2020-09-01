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
import easyconduite.media.RemoteMedia;
import easyconduite.model.AbstractUIMedia;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Tooltip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class encapsulates logics and behaviors about Custom UI Control of an
 * RemoteMedia (Kodi or VLC).
 *
 * @author A Fons
 * @since 1.0
 */
public class RemoteMediaUI extends AbstractUIMedia {

    static final Logger LOG = LogManager.getLogger(RemoteMediaUI.class);
    public static final String KODI_OK_CSS="typeKodi";
    public static final String KODI_KO_CSS="typeKodiKo";
    private final RemoteMedia remoteMedia;
    private Boolean playerReady;
    private final Tooltip typeRegionToolTip = new Tooltip();
    private String activePlayerCss;
    private String inactivePlayerCss;

    /**
     * Constructor du UI custom control for an AudioMedia.
     *
     * @param media      a media wich be play.
     */
    public RemoteMediaUI(RemoteMedia media) {
        super(media);
        LOG.info("Construct an AudioMedia {}", media);
        this.remoteMedia = media;
        setPlayerReady(false);

        remoteMedia.statutProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                LOG.trace("MediaStatus player {} is {}", this.remoteMedia.getName(), newValue);
                switch (newValue) {
                    case PAUSED:
                    case READY:
                    case STOPPED:
                        playingClass.setValue(false);
                        setReadyIcon(true);
                        break;
                    case PLAYING:
                        playingClass.setValue(true);
                        setReadyIcon(true);
                        break;
                    case UNKNOWN:
                    case HALTED:
                        setReadyIcon(false);
                        break;
                    default:
                        break;
                }
            }
        });

        Tooltip.install(typeRegion, typeRegionToolTip);

        if (remoteMedia.getType().equals(RemoteMedia.RemoteType.KODI)) {
            activePlayerCss=KODI_OK_CSS;
            inactivePlayerCss=KODI_KO_CSS;
        }
        typeRegion.getStyleClass().add(inactivePlayerCss);
        contextHbox.getChildren().add(new JFXVolumeSlider());
        this.actualizeUI();
    }

    private void setReadyIcon(boolean etat){
        if(etat){
            typeRegion.getStyleClass().remove(inactivePlayerCss);
            typeRegion.getStyleClass().add(activePlayerCss);
            playPauseHbox.setDisable(false);
        }else{
            typeRegion.getStyleClass().remove(activePlayerCss);
            typeRegion.getStyleClass().add(inactivePlayerCss);
            playPauseHbox.setDisable(true);
        }
    }

    @Override
    public void playPause() {
        switch (remoteMedia.getStatut()){
            case PAUSED:
            case READY:
            case STOPPED:
                remoteMedia.play();
                break;
            case PLAYING:
                remoteMedia.stop();
                break;
            default:
                break;
        }
    }

    @Override
    public void stop() {
        remoteMedia.stop();
    }

    @Override
    public void actualizeUI() {
        super.actualizeUI();
        typeRegionToolTip.setText(remoteMedia.getHost());
    }

    private class JFXVolumeSlider extends JFXSlider {
        private boolean changing;

        //TODO implémenter, dans le KodiManager, le changement de volume lorsque l'on relache
        protected JFXVolumeSlider() {
            super(0, 100, remoteMedia.getVolume() * 100);
            final DoubleProperty volumeProperty = RemoteMediaUI.JFXVolumeSlider.this.valueProperty();
        }
    }

    public Boolean getPlayerReady() {
        return playerReady;
    }

    public void setPlayerReady(Boolean playerReady) {
        this.playerReady = playerReady;
    }

}
