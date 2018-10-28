/*
 * Copyright (C) 2018 Antony Fons
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
package easyconduite.controllers.helpers;

import easyconduite.controllers.MainController;
import easyconduite.controllers.TrackConfigController;
import easyconduite.objects.AudioMedia;
import easyconduite.tools.Constants;
import java.util.Set;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author antony
 */
public class TrackConfigHandler {
    
    static final Logger LOG = LogManager.getLogger(TrackConfigHandler.class);
    
    private final TrackConfigController controller;
    private final MainController mainController;
    private final HBox tracksHbox;
    
    public TrackConfigHandler(TrackConfigController aThis, MainController mainController) {
        this.controller = aThis;
        this.mainController = mainController;
        this.tracksHbox = controller.getChildsTracksHbox();
    }
    
    private ObservableList<AudioMedia> getChilds(Set<UUID> childUUID, ObservableList<AudioMedia> parentsLits) {
        ObservableList<AudioMedia> childsList = FXCollections.observableArrayList();
        childsList.addAll(parentsLits.filtered((t) -> {
            return childUUID.contains(t.getUniqueId());
        }));
        return childsList;
    }
    
    private class TracksItemCallBack implements Callback<ListView<AudioMedia>, ListCell<AudioMedia>> {
        
        @Override
        public ListCell<AudioMedia> call(ListView<AudioMedia> param) {
            ListCell<AudioMedia> cell = new ListCell<AudioMedia>() {
                @Override
                protected void updateItem(AudioMedia value, boolean empty) {
                    super.updateItem(value, empty);
                    final String text = (value == null || empty) ? null : value.getName();
                    setText(text);
                    
                }
            };
            param.setOnKeyPressed((event) -> {
                LOG.trace("Key pressed");
                if (event.getCode().equals(KeyCode.DELETE)) {
                    LOG.trace("delete pressed");
                    AudioMedia media = param.getSelectionModel().getSelectedItem();
                    boolean remove = param.getItems().remove(media);
                }
            });
            return cell;
        }
    }
    
    public void buildChildsManagerView(ObservableList<AudioMedia> allMedia) {
        
        ListView<AudioMedia> avalaibleTracks = (ListView<AudioMedia>) controller.getChildsTracksHbox().lookup("#avalaibleTracks");
        
        ListView<AudioMedia> beginTracks = (ListView<AudioMedia>) controller.getChildsTracksHbox().lookup("#beginTracks");
        ListView<AudioMedia> endTracks = (ListView<AudioMedia>) controller.getChildsTracksHbox().lookup("#endTracks");
        
        avalaibleTracks.setItems(allMedia.filtered((AudioMedia t) -> t != controller.getAudioMedia()));
        avalaibleTracks.setCellFactory(new TracksItemCallBack());

        // Pour Test
        controller.getAudioMedia().getUuidChildBegin().add(controller.getAudioMedia().getUniqueId());
        controller.getAudioMedia().getUuidChildEnd().add(controller.getAudioMedia().getUniqueId());
        
        beginTracks.setItems(getChilds(controller.getAudioMedia().getUuidChildBegin(), mainController.getAudioTable().getAudioMediaList()));
        beginTracks.setCellFactory(new TracksItemCallBack());
        endTracks.setItems(getChilds(controller.getAudioMedia().getUuidChildEnd(), mainController.getAudioTable().getAudioMediaList()));
        endTracks.setCellFactory(new TracksItemCallBack());
        
        setDragDetected(avalaibleTracks, new ListView[]{beginTracks, endTracks});
        setDragDetected(endTracks, new ListView[]{avalaibleTracks});
        
    }
    
    private void setDragDetected(ListView<AudioMedia> listViewSource, ListView<AudioMedia>[] listViewChilds) {
        listViewSource.setOnDragDetected((MouseEvent event) -> {
            Object o = event.getSource();
            if (o instanceof ListView) {
                AudioMedia media = (AudioMedia) ((ListView) o).getSelectionModel().getSelectedItem();
                final Dragboard dragBroard = listViewSource.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.put(Constants.DATA_FORMAT_UUID, media.getUniqueId());
                LOG.trace("DanddD detected {}", media.getUniqueId());
                dragBroard.setContent(content);
            }
            
            for (ListView<AudioMedia> listViewChild : listViewChilds) {
                setDanDdHandler(listViewChild);
            }
            event.consume();
        });
        
    }
    
    private void setDanDdHandler(ListView<AudioMedia> listView) {
        
        listView.setOnDragOver((event) -> {
            final Dragboard dragBroard = event.getDragboard();
            Object b = event.getSource();
            if (b instanceof ListView && dragBroard.hasContent(Constants.DATA_FORMAT_UUID)) {
                LOG.trace("ListView {}",((ListView)b).getId());
                event.acceptTransferModes(TransferMode.COPY);
                UUID media = (UUID) dragBroard.getContent(Constants.DATA_FORMAT_UUID);
                LOG.trace("event {}", media.toString());
            }
            event.consume();
        });
        
        listView.setOnDragDropped((event) -> {
            event.setDropCompleted(true);
            event.consume();
        });
        
    }
}
