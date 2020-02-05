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

import easyconduite.controllers.TrackConfigController;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.project.EasyTable;
import easyconduite.objects.AudioTableWrapper;
import easyconduite.util.Constants;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author antony
 * @deprecated
 */
@Deprecated
public class TrackConfigHandler {

    static final Logger LOG = LogManager.getLogger(TrackConfigHandler.class);

    private final TrackConfigController controller;
    private ListView<UUID> dragDetectedSource;
    private final EasyTable easyTable;

    public TrackConfigHandler(TrackConfigController aThis) {
        this.controller = aThis;
        easyTable = AudioTableWrapper.getInstance().getEasyTable();
    }

    public void buildChildsManagerView(AudioMedia media) {

        final UUID uuidMedia = media.getUniqueId();

        //ListView<UUID> avalaibleTracks = controller.getAvalaibleTracks();
        ObservableList<UUID> allUUid = FXCollections.observableArrayList();
        easyTable.getAudioMediaList().forEach((AudioMedia t) -> {
            if (t.getUniqueId() != uuidMedia) {
                allUUid.add(t.getUniqueId());
            }
        });

//        avalaibleTracks.setItems(allUUid);
//        avalaibleTracks.setCellFactory(new TracksItemCallBack());

        //ListView<UUID> beginTracks = (ListView<UUID>) controller.getChildsTracksHbox().lookup("#beginTracks");
//        beginTracks.setItems(media.getUuidChildBegin());
//        beginTracks.setCellFactory(new TracksItemCallBack());
//
//        ListView<UUID> endTracks = (ListView<UUID>) controller.getChildsTracksHbox().lookup("#endTracks");
//        endTracks.setItems(media.getUuidChildEnd());
//        endTracks.setCellFactory(new TracksItemCallBack());
//
//        ListView[] listViewForDragAndDrop = new ListView[]{avalaibleTracks, beginTracks, endTracks};
//
//        for (ListView listView : listViewForDragAndDrop) {
//            setDragDetected(listView);
//            setDanDdHandler(listView);
//        }

    }

    private void setDragDetected(ListView<UUID> listeningListView) {
        listeningListView.setOnDragDetected((MouseEvent event) -> {
            Object o = event.getSource();
            if (o instanceof ListView) {
                dragDetectedSource = (ListView<UUID>) o;
                UUID uuid = (UUID) ((ListView) o).getSelectionModel().getSelectedItem();
                final Dragboard dragBroard = listeningListView.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.put(Constants.DATA_FORMAT_UUID, uuid);
                dragBroard.setContent(content);
            }
            event.consume();
        });
    }

    private void setDanDdHandler(ListView<UUID> listeningListView) {

        listeningListView.setOnDragOver((DragEvent event) -> {
            Object o = event.getSource();
            if (o instanceof ListView) {
                final Dragboard dragBroard = event.getDragboard();
                if (isListViewDraggable(listeningListView, event)) {
                    UUID media = (UUID) dragBroard.getContent(Constants.DATA_FORMAT_UUID);
                    if (!listeningListView.getItems().contains(media)) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                }
            }
            event.consume();
        });
//        listeningListView.setOnDragDropped((event) -> {
//            Object o = event.getSource();
//            if (o instanceof ListView) {
//                final Dragboard dragBroard = event.getDragboard();
//                if (isListViewDraggable(listeningListView, event)) {
//                    UUID media = (UUID) dragBroard.getContent(Constants.DATA_FORMAT_UUID);
//                    if (!listeningListView.getItems().contains(media) && !listeningListView.equals(controller.getAvalaibleTracks())) {
//                        listeningListView.getItems().add(media);
//                        event.setDropCompleted(true);
//                        LOG.trace("ChildBegin AudioMedia {} childs {}", controller.getAudioMedia().getName(), controller.getAudioMedia().getUuidChildBegin());
//                    }
//                } else {
//                    event.setDropCompleted(false);
//                }
//            }
//            event.consume();
//        });
//
//        listeningListView.setOnDragDone((event) -> {
//            Object o = event.getSource();
//            if (o instanceof ListView) {
//                final Dragboard dragBroard = event.getDragboard();
//                UUID media = (UUID) dragBroard.getContent(Constants.DATA_FORMAT_UUID);
//                if (!listeningListView.equals(controller.getAvalaibleTracks())) {
//                    dragDetectedSource.getItems().remove(media);
//                    LOG.trace("ChildBegin AudioMedia {} childs {}", controller.getAudioMedia().getName(), controller.getAudioMedia().getUuidChildBegin());
//                }
//                dragBroard.clear();
//            }
//            event.consume();
//        });

    }

    private boolean isListViewDraggable(ListView<UUID> listView, DragEvent event) {
        return listView != dragDetectedSource && event.getDragboard().hasContent(Constants.DATA_FORMAT_UUID);
    }

    private class TracksItemCallBack implements Callback<ListView<UUID>, ListCell<UUID>> {

        @Override
        public ListCell<UUID> call(ListView<UUID> param) {
            ListCell<UUID> cell = new ListCell<UUID>() {
                @Override
                protected void updateItem(UUID value, boolean empty) {
                    super.updateItem(value, empty);
                    String text;
                    AudioMedia audioMedia = easyTable.findByUUID(value);
                    LOG.trace("Value dans call {}", value);
                    if (empty || value == null || audioMedia == null) {
                        text = null;
                    } else {
                        text = audioMedia.getName();
                    }
                    setText(text);
                }
            };
            return cell;
        }
    }
}
