/*
 * Copyright (C) 2017 Antony Fons
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
package easyconduite.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class creates a clickable image with a icon.
 *
 * @author Antony Fons
 */
public class IconButton extends ImageView {

    private Image icon;

    private String pathNameOfIcon;

    private static final String CSS_STYLE = "circle-Button";

    /**
     * Constructor.<br>
     * Construct an IconButton, wich extends an {@link ImageView}. The size of icon is 30,30 px.
     *
     * @param pathNameOfIcon The path and name of the icon, loaded by ResourceAsStream (from classpath).
     */
    public IconButton(String pathNameOfIcon) {
        super();
        this.icon = new Image(getClass().getResourceAsStream(pathNameOfIcon), 30, 30, true, false);
        this.setImage(getIcon());
        this.getStyleClass().add(CSS_STYLE);
    }

    /**
     * Get the {@link  Image} icon.
     *
     * @return Image Object of IconButton.
     */
    public final Image getIcon() {
        return icon;
    }

    /**
     * Set the {@link  Image} icon.
     *
     * @param icon
     */
    public void setIcon(Image icon) {
        this.icon = icon;
    }

    /**
     * Get the pathName of the icon.
     *
     * @return
     */
    public final String getPathNameOfIcon() {
        return pathNameOfIcon;
    }

    /**
     * Set the pathName of the icon and fix {@link  Image} of this with a 30,30 px image ressource.
     *
     * @param nameOfIcon
     */
    public void setPathNameOfIcon(String nameOfIcon) {
        this.pathNameOfIcon = nameOfIcon;
        setIcon(new Image(getClass().getResourceAsStream(nameOfIcon), 30, 30, true, false));
        this.setImage(getIcon());
    }

}
