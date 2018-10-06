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
package easyconduite.ui.commons;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 *
 * @author antony
 */
public class EasyconduiteUITools {

    public static <T extends Node> Scene getScene(T node) {
        return (Scene) node.getScene();
    }

    public static void updateWindowsTitle(Scene scene, String text) {
        final Stage primStage = (Stage) scene.getWindow();
        primStage.setTitle(text);
    }

    public static <T extends Node> Window getWindow(T node) {
        return EasyconduiteUITools.getScene(node).getWindow();
    }

    public static String formatTime(Duration duration) {
        if (duration.greaterThan(Duration.ZERO)) {
            final double millis = duration.toMillis();
            final int dec = (int)((millis /100)%10);
            final int seconds = (int) ((millis / 1000)%60);
            final int minutes = (int)(millis / (1000*60));
            return String.format("%02d:%02d:%02d", minutes, seconds,dec);
        }
        return null;
    }
}
