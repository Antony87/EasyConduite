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
import easyconduite.media.MediaStatus;
import easyconduite.model.AbstractMedia;
import easyconduite.model.BaseController;
import easyconduite.view.controls.TriggerActionRegion;
import javafx.collections.ObservableList;
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
import java.util.TreeMap;

public class ConduiteController extends BaseController {

    private static final Logger LOG = LogManager.getLogger(ConduiteController.class);

    private MainController mainController;

    private Conduite conduite;

    /**
     * grid row of the media
     */
    private Map<Integer, AbstractMedia> tracksMap;

    @FXML
    private BorderPane cueBorderPane;

    private final GridPane grid;

    public ConduiteController() {
        LOG.debug("EasyConduite ConduiteController is instantiated");
        conduite = new Conduite();
        grid = new GridPane();
        grid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        tracksMap = new TreeMap<>();
    }

    @FXML
    public void addTriggerAction(ActionEvent event) {

        conduite.addNewTrigger();
        final Trigger trigger = conduite.getTriggers().get(conduite.getTriggers().lastKey());
        int index = trigger.getIndex();
        final Label titleTrigger = new Label("" + index);
        titleTrigger.getStyleClass().add("labelHeadTrigger");
        grid.add(titleTrigger, index, 0);

        tracksMap.forEach((row, abstractMedia) -> {
            addTriggerActionRegion(trigger,row,index);
//            final MediaAction action = new MediaAction(trigger,abstractMedia, MediaStatus.UNKNOWN);
//            trigger.getMediaActions().add(action);
            trigger.addNewMediaAction(abstractMedia);
        });

    }

    public void addMedia(AbstractMedia media) {

        final String name = media.getName();
        final Label titleMedia = new Label(name);
        titleMedia.getStyleClass().add("labelConduite");
        int nbrRows = grid.getRowCount();

        conduite.getTriggers().forEach((index, trigger) -> {
            addTriggerActionRegion(trigger,nbrRows,index);
        });


        grid.add(titleMedia, 0, nbrRows);
        tracksMap.put(nbrRows, media);
    }

    private void addTriggerActionRegion(Trigger trigger, int row, int column){
        final TriggerActionRegion regionTrigger = new TriggerActionRegion(row, column, grid);
        LOG.debug("region added at row: {} column: {}", regionTrigger.getRowIndex(), regionTrigger.getColIndex());

        regionTrigger.setOnMouseClicked(event -> {
            System.out.println(trigger.getIndex());
            System.out.println("media : "+tracksMap.get(row).getName());
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        cueBorderPane.setCenter(grid);
        final Label titleColonne = new Label(" Triggers : ");
        grid.add(titleColonne, 0, 0);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private Node getNodeByRowColumnIndex(final int row, final int column) {
        Node result = null;
        ObservableList<Node> childrens = grid.getChildren();

        for (Node node : childrens) {
            if (grid.getRowIndex(node) == row && grid.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }
}
