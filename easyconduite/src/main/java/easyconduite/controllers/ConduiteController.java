/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package easyconduite.controllers;

import easyconduite.conduite.Conduite;
import easyconduite.conduite.MediaAction;
import easyconduite.conduite.Trigger;
import easyconduite.model.AbstractMedia;
import easyconduite.model.BaseController;
import easyconduite.model.MediaPlayable;
import easyconduite.project.ProjectContext;
import easyconduite.view.commons.PlayingPseudoClass;
import easyconduite.view.controls.TriggerActionRegion;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConduiteController extends BaseController {

    //TODO gérer la suppression d'un trigger.

    private static final Logger LOG = LogManager.getLogger(ConduiteController.class);

    private final GridPane grid;

    /**
     * grid row of the media
     */
    private final SortedMap<Integer, MediaPlayable> tracksMap;
    @FXML
    private BorderPane cueBorderPane;

    private Conduite conduite;

    public ConduiteController() {
        LOG.debug("EasyConduite ConduiteController is instantiated");
        grid = new GridPane();
        grid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        tracksMap = new TreeMap<>();
    }

    @FXML
    public void fireNextTriggerAction(ActionEvent event) {
        conduite.fireNextTrigger();
        event.consume();
    }

    @FXML
    public void firePreviousTriggerAction(ActionEvent event) {
        conduite.firePreviousTrigger();
        event.consume();
    }

    @FXML
    public void fireRewindTriggerAction(ActionEvent event) {
        ProjectContext.getContext().getMainControler().handleStopAll(event);
        conduite.fireRewindTrigger();
        event.consume();
    }

    @FXML
    public void addTriggerAction(ActionEvent event) {
        if (!tracksMap.isEmpty()) {
            final Trigger trigger = conduite.createTrigger();
            int columnIndex = conduite.getListeTriggers().getLast().getIndex();
            addTriggerLabel(trigger, columnIndex);
            tracksMap.forEach((row, media) -> {
                final MediaAction action = trigger.getActionFromMedia(media);
                final Region actionRegion = new TriggerActionRegion(trigger, action,columnIndex,row);
                grid.add(actionRegion, columnIndex, row);
            });
        }
        event.consume();
    }

    private void addTriggerLabel(Trigger trigger, int index) {
        final Label titleTrigger = new Label("" + index);
        final BooleanProperty playingClass = new PlayingPseudoClass(titleTrigger);
        trigger.actifProperty().addListener((observable, oldValue, newValue) -> {
            playingClass.setValue(newValue);
        });
        titleTrigger.getStyleClass().add("labelHeadTrigger");
        // add an id for lookup "column,row"
        titleTrigger.setId(index+"_0");
        grid.add(titleTrigger, index, 0);
    }

    public void addMedia(MediaPlayable media) {

        final Label titleMedia = new Label();
        final AbstractMedia abstractMedia = (AbstractMedia) media;
        titleMedia.textProperty().bind(abstractMedia.nameProperty());
        titleMedia.getStyleClass().add("labelConduite");

        int lastRow=0;
        if(tracksMap.size()==0){
            lastRow=1;
        }else{
            lastRow=tracksMap.lastKey()+1;
        }

        int finalLastRow = lastRow;
        conduite.getListeTriggers().forEach(trigger -> {
            if (trigger != null) {
                int indexColumn = trigger.getIndex();
                MediaAction action = trigger.getActionFromMedia(media);
                final Region actionRegion = new TriggerActionRegion(trigger, action,indexColumn,finalLastRow);
                grid.add(actionRegion, indexColumn, finalLastRow);
            }
        });
        tracksMap.put(lastRow, media);

        // add an id for lookup "column-row"
        titleMedia.idProperty().setValue("0_"+lastRow);
        grid.add(titleMedia, 0, lastRow);
    }

    public void deleteOneMedia(MediaPlayable media){
        LOG.debug("deleteOneMedia to conduite MediaPlayable {}",media);
        // trouver le media dans la tracksMap.
        final Map.Entry<Integer,MediaPlayable> entries = tracksMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(media))
                .findFirst()
                .orElse(null);
        final int rowIndex = entries.getKey();
        final Node node = grid.getScene().lookup("#0_"+rowIndex);
        grid.getChildren().remove(node);

        // supression des TriggerActionRegion associés à la ligne du media
        conduite.getListeTriggers().forEach(trigger -> {
            final Node actionRegion = grid.lookup("#"+trigger.getIndex()+"_"+rowIndex);
            grid.getChildren().remove(actionRegion);
        });

        // suppression dans la trackMap
        tracksMap.remove(rowIndex,media);
    }

    public void updateConduiteUI(Conduite conduite){
        conduite.getListeTriggers().forEach(trigger -> {
            addTriggerLabel(trigger, trigger.getIndex());
        });
    }

    public void setConduite(Conduite conduite) {
        this.conduite = conduite;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        cueBorderPane.setCenter(grid);
        final Label titleColonne = new Label(" Triggers : ");
        titleColonne.getStyleClass().add("jfx-button");
        grid.add(titleColonne, 0, 0);

    }
}
