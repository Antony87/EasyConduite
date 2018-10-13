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
import easyconduite.util.Constants;
import easyconduite.view.AudioMediaUI;
import java.util.Objects;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author antony
 */
public class DandDMediaUiHelper {

    private static final Logger LOG = LogManager.getLogger(DandDMediaUiHelper.class);

    private AudioMediaUI sourceUi;

    private Integer sourceIndex;

    private Integer targetIndex;

    private AudioMediaUI targetUi;

    public DandDMediaUiHelper() {

    }

    public void setDragAndDropFeature(FlowPane tableLayout, MainController controler) {

        tableLayout.setOnDragDetected((MouseEvent mouseEvent) -> {
            if (mouseEvent.getTarget() instanceof AudioMediaUI) {
                final Dragboard dragBroard = tableLayout.startDragAndDrop(TransferMode.COPY);
                // Remlissage du contenu.
                ClipboardContent content = new ClipboardContent();
                // recup de l'index children
                sourceUi = (AudioMediaUI) mouseEvent.getTarget();
                sourceIndex = tableLayout.getChildren().indexOf(sourceUi);
                content.put(Constants.DATA_FORMAT_INTEGER, sourceIndex);
                dragBroard.setContent(content);
            }
            mouseEvent.consume();
        });

        tableLayout.setOnDragOver((DragEvent dragEvent) -> {
            final Dragboard dragBroard = dragEvent.getDragboard();
            if (dragEvent.getTarget() instanceof AudioMediaUI && dragBroard.hasContent(Constants.DATA_FORMAT_INTEGER)) {
                //final Integer indexSource = (Integer) dragBroard.getContent(Constants.DATA_FORMAT_INTEGER);
                targetIndex = tableLayout.getChildren().indexOf(dragEvent.getTarget());
                if (!Objects.equals(targetIndex, sourceIndex)) {
                    dragEvent.acceptTransferModes(TransferMode.COPY);
                }
            }
            dragEvent.consume();
        }
        );

        tableLayout.setOnDragDropped((DragEvent dragEvent) -> {

            if (dragEvent.getTarget() instanceof AudioMediaUI) {
                final Dragboard dragBroard = dragEvent.getDragboard();
                //final Integer idSource = (Integer) dragBroard.getContent(Constants.DATA_FORMAT_INTEGER);
                //final AudioMediaUI sourceUi = (AudioMediaUI) tableLayout.getChildren().get(idSource);

                targetUi = (AudioMediaUI) dragEvent.getTarget();
                targetIndex = tableLayout.getChildren().indexOf(targetUi);

                tableLayout.getChildren().set(sourceIndex, new VBox());
                tableLayout.getChildren().set(targetIndex, sourceUi);
                tableLayout.getChildren().set(sourceIndex, targetUi);
                dragEvent.setDropCompleted(true);
            }
            dragEvent.consume();
        }
        );

        tableLayout.setOnDragDone(dragEvent -> {
            if (dragEvent.getTransferMode() == TransferMode.COPY) {
                dragEvent.getDragboard().clear();
            }
            controler.orderAudioTable();
            dragEvent.consume();
        });
    }
}
