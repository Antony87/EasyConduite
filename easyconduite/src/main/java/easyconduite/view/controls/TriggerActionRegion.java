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

package easyconduite.view.controls;

import easyconduite.conduite.MediaAction;
import easyconduite.conduite.Trigger;
import javafx.scene.layout.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TriggerActionRegion extends Region {

    private static final Logger LOG = LogManager.getLogger(TriggerActionRegion.class);

    private final Trigger trigger;
    private final MediaAction mediaAction;
    private final int rowIndex;
    private final int columnIndex;

    public TriggerActionRegion(Trigger aTrigger, MediaAction action, int column,int row) {
        super();
        LOG.debug("Create TriggerActionRegion whith Trigger {} Media {}",aTrigger,action);
        this.trigger = aTrigger;
        this.mediaAction = action;
        this.rowIndex= row;
        this.columnIndex = column;

        updateUI(mediaAction);

        this.setOnMouseClicked(event -> {
            mediaAction.nextAction();
            updateUI(mediaAction);
        });
        this.getStyleClass().add("conduiteRegion");
        this.setId(columnIndex+"_"+rowIndex);
    }


    public void updateUI(MediaAction action){
        if(action!=null){
            this.getStyleClass().removeAll("playbutton", "pausebutton", "stopbutton");
            switch (action.getStatusAction()) {
                case STOP:
                    this.getStyleClass().add("stopbutton");
                    break;
                case PLAY:
                    this.getStyleClass().add("playbutton");
                    break;
                case PAUSE:
                    this.getStyleClass().add("pausebutton");
            }
        }
    }

    @Override
    public String toString() {
        return "TriggerActionRegion{" +
                "trigger=" + trigger +
                ", mediaAction=" + mediaAction +
                ", rowIndex=" + rowIndex +
                ", columnIndex=" + columnIndex +
                "} " + super.toString();
    }
}
