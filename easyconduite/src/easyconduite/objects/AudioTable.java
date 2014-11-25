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
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;

public class AudioTable {

        private Scene scene;

        private ObservableMap<Integer,AudioMedia> observableTable;

        private static AudioTable instance = new AudioTable();

        private AudioTable() {

                observableTable = FXCollections.observableHashMap();

                observableTable.addListener(new MapChangeListener<Integer,AudioMedia>() {
                        @Override
                        public void onChanged(Change<? extends Integer,? extends AudioMedia> change) {
                               
                                if(change.wasAdded()){
                                        change.getValueAdded().draw(scene);
                                }
                                if(change.wasRemoved()){
                                        change.getValueRemoved().remove(scene);;
                                }
                        }

                });

        }

        public static AudioTable getInstance() {
                return instance;
        }

        public void addAudioMedia(File audioFile) throws IOException {

                AudioMedia audioMedia = new AudioMedia(audioFile);
                observableTable.putIfAbsent(audioMedia.getId(), audioMedia);
        }
       
        public void deleteAudioMedia(Integer id){
                observableTable.remove(id);
        }

        public AudioMedia getAudioMedia(Integer id) {  
                return observableTable.get(id);
        }

        public Scene getScene() {
                return scene;
        }

        public void setScene(Scene scene) {
                this.scene = scene;
        }


}
