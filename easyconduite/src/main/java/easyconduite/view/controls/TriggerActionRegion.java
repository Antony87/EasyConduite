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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class TriggerActionRegion extends Region {

    private IntegerProperty colIndex = new SimpleIntegerProperty();

    private IntegerProperty rowIndex = new SimpleIntegerProperty();

    public TriggerActionRegion(Integer rowIndex, Integer colIndex, GridPane gridToAdd) {
        super();
        this.getStyleClass().add("conduiteRegion");
        colIndexProperty().setValue(colIndex);
        rowIndexProperty().setValue(rowIndex);
        gridToAdd.add(this,getColIndex(),getRowIndex());
    }

    public int getColIndex() {
        return colIndex.get();
    }

    public IntegerProperty colIndexProperty() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex.set(colIndex);
    }

    public int getRowIndex() {
        return rowIndex.get();
    }

    public IntegerProperty rowIndexProperty() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex.set(rowIndex);
    }
}
