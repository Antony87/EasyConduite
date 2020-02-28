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

import easyconduite.model.AbstractMedia;
import easyconduite.model.SpecificConfigurable;
import easyconduite.objects.media.RemotePlayer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class RemoteConfigController implements Initializable, SpecificConfigurable {

    private RemotePlayer remoteMedia;

    @FXML
    private TextField hostTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField resourceTextField;

    public RemoteConfigController(RemotePlayer media) {
        this.remoteMedia=media;
    }

    @Override
    public void updateSpecificMedia(AbstractMedia media) {
        remoteMedia= (RemotePlayer) media;

        final URI resourceURI = new File(resourceTextField.getText()).toURI();
        remoteMedia.setResource(resourceURI);
        remoteMedia.setHost(hostTextField.getText());
        remoteMedia.setPort(Integer.valueOf(portTextField.getText()));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
