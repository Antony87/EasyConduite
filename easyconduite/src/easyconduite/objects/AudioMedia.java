/*
EasyConduite Copyright 2014 Antony Fons

This file is part of EasyConduite.

EasyConduite is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or
any later version.

EasyConduite is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with EasyConduite.  If not, see <http://www.gnu.org/licenses/>.
 */
package easyconduite.objects;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

/**
 * Class that encapsulate audio media, behavior.
 *
 * @author A.Fons
 *
 */
public class AudioMedia {

        /**
         * id
         */
        private Integer id;

        /**
         * Name of track, to be displayed on top of the pane.
         *
         */
        private String name;

        private int linkedKeyCode;

        private File audioFile;

        private MediaPlayer player;

        private final DoubleProperty volume = new SimpleDoubleProperty();

        private static AtomicInteger idAudioMedia = new AtomicInteger();

        private final static String CONTROL_PATH = "/fxml/piste.fxml";

        /**
         * Default constructor, to be conform bean specification.
         */
        public AudioMedia() {

        }

        public AudioMedia(final File audioFile) {
                this.audioFile = audioFile;    
                Media media = new Media(audioFile.toURI().toString());
                player = new MediaPlayer(media);
                setVolume(0.5);
                player.volumeProperty().bindBidirectional(this.volume);
                id = createID();
        }

        public void play() {
                if(getStatus()==Status.READY || getStatus()==Status.STOPPED){
                        player.seek(Duration.ZERO);
                        player.play();
                }
                if(getStatus()==Status.PAUSED){
                        player.play();
                }
        }

        public void pause() {
                if (player.getStatus() == Status.PLAYING) {
                        player.pause();
                }
        }

        public void stop() {

                player.stop();

        }

        public Status getStatus() {
                return player.getStatus();
        }

        public void draw(Scene scene) {

                final FXMLLoader loader = new FXMLLoader(getClass().getResource(
                                CONTROL_PATH));
                FlowPane pane = null;
                try {
                        pane = loader.load();  
                        pane.setId(String.valueOf(this.id));
                } catch (IOException e) {
                        e.printStackTrace();
                }
                HBox table = (HBox) scene.lookup("#table");
                table.getChildren().add(0, pane);

        }
       
        public void remove(Scene scene){
                player.dispose();
                HBox table = (HBox) scene.lookup("#table");
                FlowPane pane = (FlowPane) scene.lookup("#"+String.valueOf(this.id));
                table.getChildren().remove(pane);      
        }

        private static Integer createID() {
                return idAudioMedia.getAndIncrement();
        }

        public Integer getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public int getLinkedKeyCode() {
                return linkedKeyCode;
        }

        public void setLinkedKeyCode(final int linkedKeyCode) {
                this.linkedKeyCode = linkedKeyCode;
        }

        public File getAudioFile() {
                return audioFile;
        }

        public final Double getVolume() {
                return volume.get();
        }

        public final void setVolume(final Double volume) {
                this.volume.set(volume);
        }

        public MediaPlayer getPlayer() {
                return player;
        }

        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result
                                + ((audioFile == null) ? 0 : audioFile.hashCode());
                result = prime * result + ((id == null) ? 0 : id.hashCode());
                result = prime * result + linkedKeyCode;
                result = prime * result + ((name == null) ? 0 : name.hashCode());
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj)
                        return true;
                if (obj == null)
                        return false;
                if (getClass() != obj.getClass())
                        return false;
                AudioMedia other = (AudioMedia) obj;
                if (audioFile == null) {
                        if (other.audioFile.getAbsolutePath() != null)
                                return false;
                } else if (!audioFile.getAbsolutePath().equals(
                                other.audioFile.getAbsolutePath()))
                        return false;
                if (id == null) {
                        if (other.id != null)
                                return false;
                } else if (!id.equals(other.id))
                        return false;
                if (linkedKeyCode != other.linkedKeyCode)
                        return false;
                if (name == null) {
                        if (other.name != null)
                                return false;
                } else if (!name.equals(other.name))
                        return false;
                return true;
        }
}
