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
package easyconduite.view.commons;

import easyconduite.tools.Constants;
import java.text.SimpleDateFormat;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chrono {

    static final Logger LOG = LogManager.getLogger(Chrono.class);
    
    private static final SimpleDateFormat CHRONO_FORMAT = new SimpleDateFormat("HH:mm:ss");
    
    private final Timeline timer;

    private final Label label;

    private long secondesDuration = 0;
        
    public Chrono(Label label) {
        this.label = label;
        CHRONO_FORMAT.setTimeZone(Constants.TZ);
        timer = new Timeline(new KeyFrame(
                Duration.seconds(1), (ActionEvent event) -> {
                    secondesDuration = ++secondesDuration;
                    this.label.setText(this.toLabel(Duration.seconds(secondesDuration)));
                    //LOG.trace("Chrono {} s", secondesDuration);
                }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    public void stop() {
        timer.stop();
    }

    public void play() {
        timer.play();
    }

    public void pause() {
        timer.pause();
    }

    public void raz() {
        timer.stop();
        timer.jumpTo(Duration.ZERO);
        secondesDuration = 0;
        this.label.setText(this.toLabel(Duration.ZERO));
    }

    private String toLabel(Duration duration) {
        return  Constants.getFormatedDuration(duration, CHRONO_FORMAT);
    }

}
