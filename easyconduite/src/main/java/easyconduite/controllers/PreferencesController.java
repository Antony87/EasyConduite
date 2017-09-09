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
import easyconduite.objects.EasyconduiteProperty;
import easyconduite.util.EasyConduitePropertiesHandler;
import easyconduite.util.LoggingUtil;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author antony
 */
public class PreferencesController extends DialogAbstractController implements Initializable {
    
    static final Logger LOG = LogManager.getLogger(PreferencesController.class);
    
    @FXML
    private ChoiceBox<Locale> prefLang;
    
    @FXML
    private ChoiceBox<Level> prefLogLevel;
    
    @FXML
    private BorderPane prefsDialogPane;
    
    public PreferencesController() {
        super();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        EasyconduiteProperty userData = EasyConduitePropertiesHandler.getInstance().getProperties();
        
        ObservableList<Locale> listeLocale = FXCollections.observableArrayList(Locale.FRENCH, Locale.ENGLISH);
        
        prefLang.setItems(listeLocale);
        prefLang.getSelectionModel().select(listeLocale.stream().filter((Locale t) -> t.getDisplayLanguage().equals(userData.getLocale().getDisplayLanguage())).findFirst().get());
             
        prefLang.setConverter(new StringConverter<Locale>() {
            @Override
            public String toString(Locale locale) {
                return locale.getDisplayLanguage();
            }        
            @Override
            public Locale fromString(String string) {
                return new Locale(string);
            }
        });
        
        ObservableList<Level> listeLevel= FXCollections.observableArrayList(Level.OFF, Level.ERROR, Level.ALL);
        prefLogLevel.setItems(listeLevel);
        prefLogLevel.getSelectionModel().select(listeLevel.stream().filter((Level t) -> t.equals(userData.getLogLevel())).findFirst().get());
        
    }
    
    @FXML
    private void handleClickOk(MouseEvent event) {
        LOG.debug("handleClickOk called");

        EasyconduiteProperty properties = EasyConduitePropertiesHandler.getInstance().getProperties();
        properties.setLocale(prefLang.getValue());
        properties.setLogLevel(prefLogLevel.getValue());
        LoggingUtil.setLog4jLevel(prefLogLevel.getValue());
        this.close(prefsDialogPane);
    }
    
    @FXML
    private void handleClickCancel(MouseEvent event) {
        
        LOG.trace("PreferencesController handleClickCancel called");
        this.close(prefsDialogPane);
    }
    
}
