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
import easyconduite.objects.AudioTable;
import easyconduite.objects.AudioTableWrapper;
import easyconduite.tools.Constants;
import easyconduite.view.AudioMediaUI;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class manage all listeners and features instead of MainController
 *
 * @author antony
 */
public class MainListenersHandler {

    private static final Logger LOG = LogManager.getLogger(MainListenersHandler.class);

    private final MainController controler;

    private Pane calque;

    private Rectangle rect;

    public MainListenersHandler(MainController controler) {
        this.controler = controler;
    }

    private void orderAudioTable() {

        Pane tableLayout = controler.getTableLayout();
        AudioTable audioTable = AudioTableWrapper.getInstance().getAudioTable();

        tableLayout.getChildren().filtered((t) -> {
            return t instanceof AudioMediaUI;
        }).forEach((t) -> {
            ((AudioMediaUI) t).getAudioMedia().setIndex(tableLayout.getChildren().indexOf(t));
        });
        audioTable.getAudioMediaList().sort((o1, o2) -> {
            return o1.getIndex() - o2.getIndex();
        });
        audioTable.setUpdated(true);
    }

    private void moveRectLayer(Pane paneOver, Rectangle rect) {
        final Double x = paneOver.getLayoutX();
        final Double y = paneOver.getLayoutY();
        rect.setLayoutX(x);
        rect.setLayoutY(y);
    }

    private void resizeRectLayer(Pane paneOver, Rectangle rect) {
        final Double width = paneOver.getWidth();
        final Double height = paneOver.getHeight();
        rect.setWidth(width);
        rect.setHeight(height);
    }

    /**
     * Method sets up Drag and Drop features to move AudioMediaUi views
     *
     * @param tableLayout
     */
    public void setDragAndDropFeature(FlowPane tableLayout) {

        final ObservableList childs = tableLayout.getChildren();

        calque = controler.getCalquePane();

        rect = new Rectangle();
        rect.getStyleClass().add("rectDragOver");

        tableLayout.setOnDragDetected((MouseEvent mouseEvent) -> {
            Object t = mouseEvent.getTarget();
            if (t instanceof AudioMediaUI) {
                final Dragboard dragBroard = tableLayout.startDragAndDrop(TransferMode.COPY);
                // Remlissage du contenu.
                ClipboardContent content = new ClipboardContent();
                // recup de l'index children
                final AudioMediaUI sourceUi = (AudioMediaUI) t;
                final Integer sourceIndex = childs.indexOf(sourceUi);
                content.put(Constants.DATA_FORMAT_INTEGER, sourceIndex);
                dragBroard.setContent(content);
                if (!calque.getChildren().contains(rect)) {
                    calque.getChildren().add(rect);
                }
                resizeRectLayer(sourceUi, rect);
                moveRectLayer(sourceUi, rect);
            }
            mouseEvent.consume();
            tableLayout.setOnDragDone(new DragDoneHandle());
        });

        tableLayout.setOnDragOver(
                (DragEvent dragEvent) -> {
                    final Dragboard dragBroard = dragEvent.getDragboard();
                    Object t = dragEvent.getTarget();
                    if (t instanceof AudioMediaUI && dragBroard.hasContent(Constants.DATA_FORMAT_INTEGER)) {
                        final Integer sourceIndex = (Integer) dragBroard.getContent(Constants.DATA_FORMAT_INTEGER);
                        final Integer targetIndex = childs.indexOf(t);
                        if (!Objects.equals(targetIndex, sourceIndex)) {
                            dragEvent.acceptTransferModes(TransferMode.COPY);
                            moveRectLayer((AudioMediaUI) t, rect);
                        }
                    }
                    dragEvent.consume();
                }
        );

        tableLayout.setOnDragDropped(
                (DragEvent dragEvent) -> {
                    Object o = dragEvent.getTarget();
                    if (o instanceof AudioMediaUI) {
                        final Dragboard dragBroard = dragEvent.getDragboard();
                        final Integer sourceIndex = (Integer) dragBroard.getContent(Constants.DATA_FORMAT_INTEGER);
                        final AudioMediaUI sourceUi = (AudioMediaUI) childs.get(sourceIndex);
                        final AudioMediaUI targetUi = (AudioMediaUI) o;
                        final Integer targetIndex = childs.indexOf(targetUi);
                        childs.set(sourceIndex, new VBox());
                        childs.set(targetIndex, sourceUi);
                        childs.set(sourceIndex, targetUi);
                        dragEvent.setDropCompleted(true);
                    }
                    if (calque.getChildren().contains(rect)) {
                        calque.getChildren().remove(rect);
                    }
                    dragEvent.consume();
                }
        );
    }

    private class DragDoneHandle implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            if (event.getTransferMode() == TransferMode.COPY) {
                event.getDragboard().clear();
            }
            LOG.trace("Drag done detected");
            orderAudioTable();
            event.consume();
        }
    }
}
