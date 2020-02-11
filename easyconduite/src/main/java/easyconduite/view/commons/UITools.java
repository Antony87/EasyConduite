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
package easyconduite.view.commons;

import easyconduite.exception.EasyconduiteException;
import easyconduite.util.Constants;
import easyconduite.util.EasyConduitePropertiesHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ResourceBundle;

/**
 *
 * @author antony
 */
public class UITools {

    private final static String VERSION = "easyconduite.version";

    public static <T extends Node> Scene getScene(T node) {
        return (Scene) node.getScene();
    }

    /**
     *
     * @param node
     * @param addingText
     */
    public static void updateWindowsTitle(Node node, String addingText) {
        
        final Stage primStage = (Stage) getScene(node).getWindow();
        ResourceBundle userdatas = null;
        try {
            userdatas = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        } catch (EasyconduiteException e) {
            e.printStackTrace();
        }
        final ResourceBundle local = ResourceBundle.getBundle(Constants.RESOURCE_BASENAME, userdatas.getLocale());
        final String version = local.getString(VERSION);
        String title = "Easyconduite";
        if (!version.isEmpty()) {
            title = title + " v " + version + " : " + addingText;
        } else {
            title = title + " : " + addingText;
        }
        primStage.setTitle(title);
    }

    public static <T extends Node> Window getWindow(T node) {
        return UITools.getScene(node).getWindow();
    }

}
