/*
 * Copyright (C) 2015 Antony Fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.objects;

import easyconduite.model.AbstractPlayer;
import javafx.scene.input.KeyCode;

/**
 *
 * @author V902832
 */
public class Line {
    
    private String name;
    
    private KeyCode assignedKey;
    
    private Boolean highLight;
    
    private AbstractPlayer player;

    public Line() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KeyCode getAssignedKey() {
        return assignedKey;
    }

    public void setAssignedKey(KeyCode assignedKey) {
        this.assignedKey = assignedKey;
    }

    public Boolean getHighLight() {
        return highLight;
    }

    public void setHighLight(Boolean highLight) {
        this.highLight = highLight;
    }

    public AbstractPlayer getPlayer() {
        return player;
    }

    public void setPlayer(AbstractPlayer player) {
        this.player = player;
    }
    
    
    
}
