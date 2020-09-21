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
    private final GridPane grid;
    private final Conduite conduite;
    /**
     * grid row of the media
     */
    private final Map<Integer, MediaPlayable> tracksMap;
    @FXML
    private BorderPane cueBorderPane;

    public ConduiteController() {
        LOG.debug("EasyConduite ConduiteController is instantiated");
        conduite = new Conduite();
        grid = new GridPane();
        grid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        tracksMap = new TreeMap<>();
    }

    @FXML
    public void fireNextTriggerAction(ActionEvent event){
        conduite.fireNextTrigger();
        event.consume();
    }

    @FXML
    public void firePreviousTriggerAction(ActionEvent event){
        conduite.firePreviousTrigger();
        event.consume();
    }

    @FXML
    public void fireRewindTriggerAction(ActionEvent event){
        ProjectContext.getContext().getMainControler().handleStopAll(event);
        conduite.fireRewindTrigger();
        event.consume();
    }

    @FXML
    public void addTriggerAction(ActionEvent event) {
        if (!tracksMap.isEmpty()) {
            final Trigger trigger = conduite.createTrigger();
            int index = conduite.getTriggers().lastKey();
            final Label titleTrigger = new Label("" + index);

            titleTrigger.setOnMouseClicked(event1 -> trigger.runActions());

            final BooleanProperty playingClass = new PlayingPseudoClass(titleTrigger);
            trigger.actifProperty().addListener((observable, oldValue, newValue) -> {
                playingClass.setValue(newValue);
            });

            titleTrigger.getStyleClass().add("labelHeadTrigger");
            grid.add(titleTrigger, index, 0);
            tracksMap.forEach((row, media) -> {
                trigger.createMediaAction(media);
                addTriggerActionRegion(trigger, media, row, index);
            });
        }
        event.consume();
    }

    public void addMedia(MediaPlayable media) {

        final Label titleMedia = new Label();
        final AbstractMedia abstractMedia = (AbstractMedia) media;
        titleMedia.textProperty().bind(abstractMedia.nameProperty());
        titleMedia.getStyleClass().add("labelConduite");

        int nbrRows = grid.getRowCount();
        conduite.getTriggers().forEach((index, trigger) -> {
            if(trigger!=null){
                trigger.createMediaAction(media);
                addTriggerActionRegion(trigger, media, nbrRows, index);
            }
        });

        grid.add(titleMedia, 0, nbrRows);
        tracksMap.put(nbrRows, media);
    }

    private void addTriggerActionRegion(Trigger trigger, MediaPlayable media, int row, int column) {
        final TriggerActionRegion regionTrigger = new TriggerActionRegion(trigger, media, row, column, grid);
        LOG.debug("region added at row: {} column: {}", regionTrigger.getRowIndex(), regionTrigger.getColIndex());
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
