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

package easyconduite.controllers;

import easyconduite.exception.EasyconduiteException;
import easyconduite.model.*;
import easyconduite.project.ProjectContext;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.KeyCodeHelper;
import easyconduite.view.controls.ActionDialog;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.ResourceBundle;

public class CommonConfigController extends BaseController implements UImediaConfigurable {

    protected static final String KEY_ASSIGN_ERROR = "trackconfigcontroller.key.error";
    protected static final String KEY_ASSIGN_OTHER = "trackconfigcontroller.key.other";
    private static final ProjectContext context = ProjectContext.getContext();
    private KeyCode newKeyCode;

    private UIMediaPlayable mediaUI;

    @FXML
    private TextField nametrackfield;

    @FXML
    private TextField keytrackfield;

    @FXML
    private CheckBox loopTrack;

    @FXML
    private void handleClickKeyField(MouseEvent event) {
        keytrackfield.clear();
    }

    @FXML
    private void handleKeyReleasedTrack(KeyEvent event) {
        final KeyCode typedKeycode = event.getCode();
        if (isKeyCodeExist(typedKeycode)) {
            keytrackfield.clear();
            try {
                final ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
                ActionDialog.showWarning(String.format(bundle.getString(KEY_ASSIGN_ERROR), KeyCodeHelper.toString(typedKeycode)), bundle.getString(KEY_ASSIGN_OTHER));
            } catch (EasyconduiteException e) {
                //FIXME
                e.printStackTrace();
            }
        } else if (mediaUI != null) {
            final MediaPlayable media = this.mediaUI.getMediaPlayable();
            if (typedKeycode != media.getKeycode()) {
                this.newKeyCode = typedKeycode;
                keytrackfield.setText(KeyCodeHelper.toString(typedKeycode));
            }
        }
    }

    private boolean isKeyCodeExist(KeyCode keyCode) {
        final List<UIMediaPlayable> mediaUIList = context.getMainControler().getMediaUIList();
        for (UIMediaPlayable ui : mediaUIList) {
            final KeyCode code = ui.getMediaPlayable().getKeycode();
            if (code != null && code.equals(keyCode)) return true;
        }
        return false;
    }

    protected void updateCommonsValues(AbstractMedia media) {
        media.setLoppable(loopTrack.isSelected());
        media.setName(nametrackfield.getText());
        if (newKeyCode != null) {
            media.setKeycode(newKeyCode);
        }
    }

    public TextField getNametrackfield() {
        return nametrackfield;
    }

    public CheckBox getLoopTrack() {
        return loopTrack;
    }

    public KeyCode getNewKeyCode() {
        return newKeyCode;
    }

    @Override
    public void updateUI(UIMediaPlayable mediaUI) {
        this.mediaUI = mediaUI;
        MediaPlayable media = this.mediaUI.getMediaPlayable();
        nametrackfield.setText(media.getName());
        keytrackfield.setText(KeyCodeHelper.toString(media.getKeycode()));
        loopTrack.setSelected(media.getLoppable());
    }

}
