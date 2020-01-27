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

import easyconduite.model.EasyMedia;
import easyconduite.model.IEasyMediaUI;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.view.AudioMediaUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class TrackPropertiesController extends StackPane implements Initializable {

    @FXML
    private TextField nametrackfield;

    @FXML
    private CheckBox repeattrack;

    @FXML
    private Spinner fadeInSpinner;

    @FXML
    private Spinner fadeOutSpinner;

    private MainController mainController;

    private IEasyMediaUI mediaUI;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSpinners(fadeInSpinner, fadeOutSpinner);
    }

    @FXML
    private void propertiesCancelButton(ActionEvent event){
        updateTrackProperties(this.mediaUI.getEasyMedia());
    }

    @FXML
    private void propertiesApplyButton(ActionEvent event){
        EasyMedia media = this.mediaUI.getEasyMedia();
        media.setName(nametrackfield.getText());
        media.setLoppable(repeattrack.isSelected());
        if(media instanceof AudioVideoMedia){
            Integer iValueFadeIn = (Integer) fadeInSpinner.getValue();
            ((AudioVideoMedia)media).setFadeInDuration(Duration.seconds(iValueFadeIn));
            Integer iValueFadeOut = (Integer) fadeOutSpinner.getValue();
            ((AudioVideoMedia)media).setFadeOutDuration(Duration.seconds(iValueFadeOut));
        }
        this.mediaUI.updateUI();
    }

    private void updateTrackProperties(EasyMedia media){
        nametrackfield.setText(media.getName());
        repeattrack.setSelected(media.getLoppable());
        if (media instanceof AudioVideoMedia) {
            final AudioVideoMedia audioVideoMedia = (AudioVideoMedia) media;
            if (audioVideoMedia.getFadeInDuration() != null) {
                fadeInSpinner.getValueFactory().setValue((int) audioVideoMedia.getFadeInDuration().toSeconds());
            }
            if (audioVideoMedia.getFadeOutDuration() != null) {
                fadeOutSpinner.getValueFactory().setValue((int) audioVideoMedia.getFadeOutDuration().toSeconds());
            }
        }
    }

    public void setMediaUI(AudioMediaUI mediaUI) {
        this.mediaUI = mediaUI;
        updateTrackProperties((EasyMedia) this.mediaUI.getEasyMedia());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void initializeSpinners(Spinner<Integer> fadeIn, Spinner<Integer> fadeOut) {
        SpinnerValueFactory<Integer> valueFadeInFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeIn.setValueFactory(valueFadeInFactory);
        SpinnerValueFactory<Integer> valueFadeOutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeOut.setValueFactory(valueFadeOutFactory);
    }


}
