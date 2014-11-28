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
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.scene.Scene;

public class AudioTable {

    private Scene scene;
    
    private static ObservableList<AudioMedia> audioMediaObsList;

    private static AudioTable instance = new AudioTable();

    private AudioTable() {
        
        audioMediaObsList = FXCollections.observableArrayList();

    }

    public static AudioTable getInstance() {
        return instance;
    }

    public void addAudioMedia(File audioFile) throws IOException {

        AudioMedia audioMedia = new AudioMedia(audioFile);

    }

    public void deleteAudioMedia(Integer id) {

    }

    public AudioMedia getAudioMedia(Integer id) {
        return null;

    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public static ObservableList<AudioMedia> getAudioMediaObsList() {
        return audioMediaObsList;
    }

}
