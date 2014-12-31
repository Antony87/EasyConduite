/*
 * Copyright (C) 2014 antony Fons
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
package easyconduite.ui;

import easyconduite.ui.model.EasyConduiteAbstractDialog;
import easyconduite.controllers.EasyconduiteController;
import easyconduite.objects.AudioTable;
import easyconduite.util.DurationUtil;
import java.io.IOException;
import java.time.Duration;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * This class draws a dialog box to set params for the play (duration and
 * other).
 *
 * @author antony Fons
 */
public class ParamConduiteDialog extends EasyConduiteAbstractDialog {
    private static final String PATH_FXML = "/easyconduite/ressources/conduiteDialog.fxml";

    private Duration duration;

    private TextField durationText;

    private final EasyconduiteController easyConduiteController;


    /**
     * Constructor.<br>This class extends abstract class
     * {@link EasyConduiteAbstractDialog}, so you have to implement super() in
     * constructor.
     *
     * @param audioTable
     * @param controller
     * @throws IOException
     */
    public ParamConduiteDialog(final AudioTable audioTable, final EasyconduiteController controller) throws IOException {

        super(PATH_FXML, "Propriétes du spectacle");

        this.easyConduiteController = controller;

        durationText = (TextField) this.getScene().lookup("#durationField");

        durationText.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!DurationUtil.isParsableToDuration(newValue)) {
                durationText.setStyle("-fx-border-color:red");
            } else {
                durationText.setStyle("");
                duration = DurationUtil.parseFromConduite(newValue);
            }
        });

    }

    @Override
    public void onClickOkButton() {
        easyConduiteController.updateConduiteDuration(duration);
    }

    @Override
    public void onClickCancelButton() {
        this.close();
    }
}
