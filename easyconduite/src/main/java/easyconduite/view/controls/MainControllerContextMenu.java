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

import easyconduite.controllers.MainController;
import easyconduite.exception.EasyconduiteException;
import easyconduite.project.ProjectContext;
import easyconduite.util.EasyConduitePropertiesHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.FlowPane;

import java.util.ResourceBundle;

/**
 * This class implements Contextual menus for the main FlowPan of project.
 * <p>
 * Actions is :
 * <ul>
 *     <li>Import an file to a track</li>
 *     <li>Clear all project, erase all tracks</li>
 *     <li>Add all tracks to cue</li>
 * </ul>
 */
public class MainControllerContextMenu extends ContextMenu {

    public MainControllerContextMenu() throws EasyconduiteException {
        super();
        MainController controller = ProjectContext.getContext().getMainControler();
        ResourceBundle locale = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
        final MenuItem cmTitle = new MenuItem(locale.getString("menu.table.title"));
        cmTitle.setDisable(true);
        final SeparatorMenuItem cmSeparator = new SeparatorMenuItem();
        final MenuItem cmImportTrack = new MenuItem(locale.getString("menu.track.import"));
        cmImportTrack.setOnAction(controller::menuImportAudio);
        final MenuItem cmCloseProject = new MenuItem(locale.getString("menu.project.clear"));
        cmCloseProject.setOnAction(e -> {
            controller.menuCloseProject(e);
        });
        final MenuItem cmAddAllToCue = new MenuItem(locale.getString("menu.table.addToCue"));
        cmAddAllToCue.setDisable(true);
        this.getItems().addAll(cmTitle, cmSeparator, cmImportTrack, cmCloseProject, cmAddAllToCue);

        final FlowPane tableLayout = controller.getTableLayout();
        tableLayout.setOnContextMenuRequested(contextMenuEvent -> {
            if (contextMenuEvent.getTarget().equals(tableLayout)) {
                this.show(tableLayout, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            }
        });
    }
}
