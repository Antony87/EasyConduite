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
package easyconduite.controllers;

import easyconduite.model.DialogAbstractController;
import easyconduite.model.EasyAudioChain;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioMediaConfigurator;
import easyconduite.ui.commons.ActionDialog;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.KeyCodeUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is the controller for TrackConfigDialog.
 *
 * @author antony
 */
public class TrackConfigController extends DialogAbstractController implements Initializable, EasyAudioChain {

    static final Logger LOG = LogManager.getLogger(TrackConfigController.class);
    private static final String KEY_ASSIGN_ERROR = "trackconfigcontroller.key.error";
    private static final String KEY_ASSIGN_OTHER = "trackconfigcontroller.key.other";

    private MainController mainController;

    private KeyCode newKeyCode;

    @FXML
    ListView<AudioMedia> avalaibleTracks;

    @FXML
    private BorderPane trackConfigPane;

    @FXML
    private TextField nametrackfield;

    @FXML
    private TextField keytrackfield;

    @FXML
    private CheckBox repeattrack;

    @FXML
    private Spinner fadeInSpinner;

    @FXML
    private Spinner fadeOutSpinner;

    private EasyAudioChain nextChain;

    private AudioMedia audioMedia;

    private final AudioMediaConfigurator mediaConfigurator;

    public TrackConfigController() {
        super();
        mediaConfigurator = new AudioMediaConfigurator();
    }

    /**
     * Initializes controller after DialogPane loaded.<br>
     * Juts setting up the spinners.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.trace("TrackConfigController initialized");
        
        Service service = new MainCtrlNotNull();
        service.setOnSucceeded((event) -> {
            LOG.trace("listView populated called");

            nametrackfield.setText(audioMedia.getName());
            keytrackfield.setText(KeyCodeUtil.toString(audioMedia.getKeycode()));
            repeattrack.setSelected(audioMedia.getRepeatable());
            
            initializeSpinners(fadeInSpinner, fadeOutSpinner, audioMedia);

            ObservableList<AudioMedia> liste = mainController.audioTable.getAudioMediaList();         
            avalaibleTracks.setItems(liste.filtered((t) -> {
                return t!=audioMedia;
            }));
            avalaibleTracks.setCellFactory(TextFieldListCell.forListView(new MediaConverter()));
            service.cancel();
        });

        service.start();

    }

    class MediaConverter extends StringConverter<AudioMedia> {

        @Override
        public String toString(AudioMedia object) {
            return object.getName();
        }

        @Override
        public AudioMedia fromString(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @FXML
    private void handleClickOk(MouseEvent event) {

        Integer iValueFadeOut = (Integer) fadeOutSpinner.getValue();
        Integer iValueFadeIn = (Integer) fadeInSpinner.getValue();
        mediaConfigurator
                .withName(nametrackfield.getText())
                .withfadeIn(Duration.seconds(iValueFadeIn))
                .withfadeOut(Duration.seconds(iValueFadeOut));
        this.updateFromAudioMedia(audioMedia);
        this.close(trackConfigPane);
    }

    @FXML
    private void handleClickCancel(MouseEvent event) {
        this.close(trackConfigPane);
    }

    @FXML
    private void handleClickRepeat(MouseEvent event) {
        mediaConfigurator.withRepeat(repeattrack.selectedProperty().getValue());
    }

    @FXML
    private void handleClickKeyField(MouseEvent event) {
        mediaConfigurator.withKeyCodeChanged(null);
        keytrackfield.clear();
    }

    @FXML
    private void handleKeyReleasedTrack(KeyEvent event) {
        final KeyCode typedKeycode = event.getCode();
        if (mainController.isKeyCodeExist(typedKeycode)) {
            keytrackfield.clear();
            final ResourceBundle bundle = EasyConduitePropertiesHandler.getInstance().getLocalBundle();
            ActionDialog.showWarning(String.format(bundle.getString(KEY_ASSIGN_ERROR), KeyCodeUtil.toString(typedKeycode)), bundle.getString(KEY_ASSIGN_OTHER));
        } else {
            if (typedKeycode != audioMedia.getKeycode()) {
                this.newKeyCode = typedKeycode;
                keytrackfield.setText(KeyCodeUtil.toString(typedKeycode));
                mediaConfigurator.withKeyCodeChanged(newKeyCode);
            }
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setAudioMedia(AudioMedia audioMedia) {
        this.audioMedia = audioMedia;
    }

    @Override
    public void setNext(EasyAudioChain next) {
        this.nextChain = next;
    }

    @Override
    public void updateFromAudioMedia(AudioMedia media) {
        mediaConfigurator.update(audioMedia);
        setNext(mainController);
        nextChain.updateFromAudioMedia(audioMedia);
    }

    @Override
    public void removeChild(AudioMedia audioMedia) {
    }

    private void initializeSpinners(Spinner<Integer> fadeIn, Spinner<Integer> fadeOut, AudioMedia audioMedia) {
        
        SpinnerValueFactory<Integer> valueFadeInFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeIn.setValueFactory(valueFadeInFactory);
        SpinnerValueFactory<Integer> valueFadeOutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        fadeOut.setValueFactory(valueFadeOutFactory);
        if (audioMedia.getFadeInDuration() != null) {
            fadeIn.getValueFactory().setValue((int) audioMedia.getFadeInDuration().toSeconds());
        }
        if (audioMedia.getFadeOutDuration() != null) {
            fadeOut.getValueFactory().setValue((int) audioMedia.getFadeOutDuration().toSeconds());
        }
    }

    private class MainCtrlNotNull extends Service {

        @Override
        protected Task<Object> createTask() {
            return new Task<Object>() {
                @Override
                protected Object call() throws Exception {
                    LOG.trace("Task called");
                    while (trackConfigPane.getChildren().contains(null) && mainController == null) {

                    }
                    LOG.trace("object not null");
                    return mainController;
                }
            };
        }
    }
}
