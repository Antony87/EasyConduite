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

package easyconduite.view.commons;

import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;

public class MediaSelectedPseudoClass extends BooleanPropertyBase {

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("mediaselected");

    private Node node;

    public MediaSelectedPseudoClass(Node n) {
        super();
        this.node = n;
    }

    @Override
    public void invalidated() {
        node.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
    }

    @Override
    public Object getBean() {
        return node;
    }

    @Override
    public String getName() {
        return "mediaselected";
    }
}
