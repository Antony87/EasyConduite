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

import easyconduite.view.AudioMediaUI;
import easyconduite.util.Constants;
import java.util.Objects;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author antony
 */
public class DragAndDropHelper {

    public static void setDragAndDropFeature(FlowPane tableLayout) {

        tableLayout.setOnDragDetected((MouseEvent mouseEvent) -> {
            if (mouseEvent.getTarget() instanceof AudioMediaUI) {
                final Dragboard dragBroard = tableLayout.startDragAndDrop(TransferMode.COPY);
                // Remlissage du contenu.
                ClipboardContent content = new ClipboardContent();
                // recup de l'index children
                final Integer index = tableLayout.getChildren().indexOf(mouseEvent.getTarget());
                content.put(Constants.DATA_FORMAT_INTEGER, index);
                dragBroard.setContent(content);
                mouseEvent.consume();
            }
        });

        tableLayout.setOnDragOver((DragEvent dragEvent) -> {
            final Dragboard dragBroard = dragEvent.getDragboard();
            if (dragEvent.getTarget() instanceof AudioMediaUI && dragBroard.hasContent(Constants.DATA_FORMAT_INTEGER)) {
                final Integer indexSource = (Integer) dragBroard.getContent(Constants.DATA_FORMAT_INTEGER);
                final Integer index = tableLayout.getChildren().indexOf(dragEvent.getTarget());
                if (!Objects.equals(index, indexSource)) {
                    dragEvent.acceptTransferModes(TransferMode.COPY);
                }
            }
            dragEvent.consume();
        }
        );

        tableLayout.setOnDragDropped((DragEvent dragEvent) -> {

            if (dragEvent.getTarget() instanceof AudioMediaUI) {
                final Dragboard dragBroard = dragEvent.getDragboard();
                final Integer idSource = (Integer) dragBroard.getContent(Constants.DATA_FORMAT_INTEGER);
                final AudioMediaUI sourceUi = (AudioMediaUI) tableLayout.getChildren().get(idSource);
                tableLayout.getChildren().set(idSource, new VBox());
                tableLayout.getChildren().set(tableLayout.getChildren().indexOf(dragEvent.getTarget()), sourceUi);
                tableLayout.getChildren().set(idSource, (AudioMediaUI) dragEvent.getTarget());
                dragEvent.setDropCompleted(true);
                dragEvent.consume();
            }

        }
        );

        tableLayout.setOnDragDone(dragEvent
                -> {
            if (dragEvent.getTransferMode() == TransferMode.COPY) {
                dragEvent.getDragboard().clear();
            }
            dragEvent.consume();
        }
        );

    }

}
