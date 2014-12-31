/*
 * Copyright (C) 2014 antony fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.ui.model;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This abstract class implement minimal features for a dialog box.<br>
 * It builds a dialog box from a fxml file.
 *
 * @author antony fons
 */
public abstract class EasyConduiteAbstractDialog extends Stage {

    private final BorderPane dialogPane;

    /**
     * Cancel Button.
     */
    public Button annuler;

    /**
     * Ok Button.
     */
    public Button ok;

    /**
     * Constructor.
     *
     * @param fxmlPath The path of fxml descriptor for the dialog box.
     * @param title the title of the dialog box.
     * @throws IOException
     */
    public EasyConduiteAbstractDialog(final String fxmlPath, final String title) throws IOException {

        super();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        dialogPane = (BorderPane) loader.load();

        //Stage dialogStage = new Stage();
        this.setTitle(title);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);

        this.setScene(new Scene(dialogPane));

        annuler = (Button) this.getScene().lookup("#cancelbutton");
        ok = (Button) this.getScene().lookup("#okbutton");

        annuler.setOnMouseClicked((MouseEvent event) -> {
            onClickCancelButton();
        });

        ok.setOnMouseClicked((MouseEvent event) -> {
            onClickOkButton();
        });

        this.showAndWait();

    }

    /**
     * This method is calling when a click event is fired on Ok button.
     */
    public abstract void onClickOkButton();

    /**
     * This method is calling when a click event is fired on Cancel button.
     */
    public abstract void onClickCancelButton();
}
