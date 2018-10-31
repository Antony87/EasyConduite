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

import easyconduite.controllers.helpers.TrackConfigHandler;
import easyconduite.model.DialogAbstractController;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioMediaConfigurator;
import easyconduite.tools.ApplicationPropertiesHelper;
import easyconduite.tools.KeyCodeHelper;
import easyconduite.view.controls.ActionDialog;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import easyconduite.model.ChainingUpdater;

/**
 * This class is the controller for TrackConfigDialog.
 *
 * @author antony
 */
public class TrackConfigController extends DialogAbstractController implements Initializable, ChainingUpdater {

    static final Logger LOG = LogManager.getLogger(TrackConfigController.class);
    private static final String KEY_ASSIGN_ERROR = "trackconfigcontroller.key.error";
    private static final String KEY_ASSIGN_OTHER = "trackconfigcontroller.key.other";

    private MainController mainController;

    private KeyCode newKeyCode;
    
    @FXML
    private HBox childsTracksHbox;
    
    @FXML
    private ListView<UUID> avalaibleTracks;
    
    @FXML
    private ListView<UUID> endTracks;
    
    @FXML
    private ListView<UUID> beginTracks;

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

    private ChainingUpdater nextChainingElement;

    private AudioMedia audioMedia;

    private final AudioMediaConfigurator mediaConfigurator;

    private TrackConfigHandler configListenerHandler;

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
            keytrackfield.setText(KeyCodeHelper.toString(audioMedia.getKeycode()));
            repeattrack.setSelected(audioMedia.getRepeatable());
            initializeSpinners(fadeInSpinner, fadeOutSpinner, audioMedia);
            configListenerHandler = new TrackConfigHandler(this);
            configListenerHandler.buildChildsManagerView(audioMedia);
            service.cancel();
        });
        service.start();
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
            final ResourceBundle bundle = ApplicationPropertiesHelper.getInstance().getLocalBundle();
            ActionDialog.showWarning(String.format(bundle.getString(KEY_ASSIGN_ERROR), KeyCodeHelper.toString(typedKeycode)), bundle.getString(KEY_ASSIGN_OTHER));
        } else {
            if (typedKeycode != audioMedia.getKeycode()) {
                this.newKeyCode = typedKeycode;
                keytrackfield.setText(KeyCodeHelper.toString(typedKeycode));
                mediaConfigurator.withKeyCodeChanged(newKeyCode);
                mainController.updateKeyCodeList();
            }
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setAudioMedia(AudioMedia audioMedia) {
        this.audioMedia = audioMedia;
    }

    public AudioMedia getAudioMedia() {
        return audioMedia;
    }
    
    public ListView<UUID> getAvalaibleTracks() {
        return avalaibleTracks;
    }

    public HBox getChildsTracksHbox() {
        return childsTracksHbox;
    }
    
    @Override
    public void setNext(ChainingUpdater next) {
        this.nextChainingElement = next;
    }

    @Override
    public void updateFromAudioMedia(AudioMedia media) {
        mediaConfigurator.update(audioMedia);
        this.setNext(mainController);
        nextChainingElement.updateFromAudioMedia(audioMedia);
    }

    @Override
    public void removeChild(ChainingUpdater audioMedia) {
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
