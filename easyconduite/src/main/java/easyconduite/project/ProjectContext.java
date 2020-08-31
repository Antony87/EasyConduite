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

package easyconduite.project;

import easyconduite.controllers.MainController;

import java.util.HashMap;

public class ProjectContext {

    private static ProjectContext instance;
    private static final String MAIN_CTRL = "maincontroler";
    private HashMap<String, Object> context;

    private static Boolean needToSave =false;

    private ProjectContext() {
        context = new HashMap<>();
    }

    public static ProjectContext getContext() {
        if (instance == null) {
            synchronized (ProjectContext.class) {
                instance = new ProjectContext();
            }
        }
        return instance;
    }

    public MainController getMainControler() {
        return (MainController) context.get(MAIN_CTRL);
    }

    public void putMainControler(MainController mainController) {
        context.putIfAbsent(MAIN_CTRL, mainController);
    }

    public Boolean isNeedToSave() {
        return needToSave;
    }

    public void setNeedToSave(Boolean needToSave) {
        ProjectContext.needToSave = needToSave;
    }
}
