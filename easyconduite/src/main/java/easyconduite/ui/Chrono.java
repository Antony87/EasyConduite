/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyconduite.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Chrono {

    public static Timeline getTimeline(final Label label) {

        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        final Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, 0, 0, 0);

        final Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(1), (ActionEvent event) -> {
                    calendar.add(Calendar.SECOND, 1);
                    label.setText(format.format(calendar.getTime()));
                }));
        timeline.setCycleCount(Animation.INDEFINITE);

        return timeline;

    }

}
