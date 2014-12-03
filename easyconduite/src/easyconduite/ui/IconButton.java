/*
 * Copyright (C) 2014 Antony Fons
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
package easyconduite.ui;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * This class creates a clickable image with a icon.
 *
 * @author Antony Fons
 */
public class IconButton extends ImageView {
    
    private Image icon;
    
    private String nameOfIcon;

    public IconButton(String nameOfIcon) {
        super();
        this.icon = new Image(getClass().getResourceAsStream(nameOfIcon), 30, 30, true, false);
        this.setImage(getIcon());
        //this.getStyleClass().add("circleButton");
        DropShadow shadow = new DropShadow();
//Adding the shadow when the mouse cursor is on
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            this.setEffect(shadow);
        });
        this.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            this.setEffect(null);
        });
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public String getNameOfIcon() {
        return nameOfIcon;
    }

    public void setNameOfIcon(String nameOfIcon) {
        this.nameOfIcon = nameOfIcon;
        setIcon(new Image(getClass().getResourceAsStream(nameOfIcon), 30, 30, true, false));
    }
    
    

}
