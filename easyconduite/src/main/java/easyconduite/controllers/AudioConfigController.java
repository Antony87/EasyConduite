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

import easyconduite.media.AudioMedia;
import easyconduite.model.BaseController;
import easyconduite.model.UImediaConfigurable;
import easyconduite.model.UIMediaPlayable;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AudioConfigController extends BaseController implements UImediaConfigurable {

    static final Logger LOG = LogManager.getLogger(AudioConfigController.class);

    private UIMediaPlayable mediaUI;

    @FXML
    private Spinner<Integer> fadeInSpinner;

    @FXML
    private Spinner<Integer> fadeOutSpinner;

    @FXML
    private GridPane commonConfig;

    @FXML
    private VBox trackConfigVbox;

    @FXML
    private CommonConfigController commonConfigController;

    @FXML
    private void handleClickOk(MouseEvent event) {
        LOG.trace("ok clicked whith AudioMediaUI {}",mediaUI);
        final AudioMedia media = (AudioMedia) mediaUI.getMediaPlayable();

        commonConfigController.updateCommonsValues(media);
        media.setFadeInDuration(new Duration(fadeInSpinner.getValue()*1000));
        media.setFadeOutDuration(new Duration(fadeOutSpinner.getValue()*1000));
        mediaUI.actualizeUI();
        LOG.debug("Media changed {}", media);
        this.close();
    }

    @FXML
    private void handleClickCancel(MouseEvent event) {
        this.close();
    }

    private void close() {
        final Stage stage = (Stage) trackConfigVbox.getScene().getWindow();
        stage.close();
    }

    private void initializeSpinners(AudioMedia audioMedia) {

        final SpinnerValueFactory<Integer> valueFadeInFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeInSpinner.setValueFactory(valueFadeInFactory);
        final SpinnerValueFactory<Integer> valueFadeOutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeOutSpinner.setValueFactory(valueFadeOutFactory);
        if (audioMedia.getFadeInDuration() != null) {
            fadeInSpinner.getValueFactory().setValue((int) audioMedia.getFadeInDuration().toSeconds());
        }
        if (audioMedia.getFadeOutDuration() != null) {
            fadeOutSpinner.getValueFactory().setValue((int) audioMedia.getFadeOutDuration().toSeconds());
        }
    }

    @Override
    public void updateUI(UIMediaPlayable mediaUI) {
        LOG.trace("Configuration ConfigDialog {}",mediaUI);
        this.mediaUI = mediaUI;
        final AudioMedia media = (AudioMedia) this.mediaUI.getMediaPlayable();
        initializeSpinners(media);
        commonConfigController.updateUI(mediaUI);
    }

}
