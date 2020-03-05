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
import easyconduite.media.AudioMedia;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class AudioConfigController implements Initializable, SpecificConfigurable {

    private AudioMedia media;

    @FXML
    private Spinner<Integer> fadeInSpinner;

    @FXML
    private Spinner<Integer> fadeOutSpinner;

    public AudioConfigController(AudioMedia media) {
        this.media = media;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSpinners(media);
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
    public void updateSpecificMedia(AbstractMedia media) {
        final Integer iValueFadeOut = fadeOutSpinner.getValue();
        final Integer iValueFadeIn = fadeInSpinner.getValue();
        ((AudioMedia) media).setFadeInDuration(new Duration(iValueFadeIn * 1000));
        ((AudioMedia) media).setFadeOutDuration(new Duration(iValueFadeOut * 1000));
    }

}
