/*
 * Copyright (C) 2015 antony
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


import java.time.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author antony
 * @deprecated 
 */
public class CursorTimeLineConduite {

    private static Duration duration;

    private static Rectangle rectangle;

    private static StackPane pane;

    private Timeline timeLine;

    private Line curseur;

    private Double pas;

    private CursorTimeLineConduite() {

        if (duration != null && !duration.equals(Duration.ZERO)) {

            final Double width = rectangle.getWidth();

            curseur = new Line(rectangle.getX(), rectangle.getY() + 2, rectangle.getX(), rectangle.getY() + rectangle.getHeight() - 2);
            curseur.setStroke(Color.FIREBRICK);
            curseur.strokeWidthProperty().set(3);

            pane.getChildren().add(curseur);

            pas = Math.rint(width / (duration.toMinutes() * 60));
            int count = (int) (duration.toMinutes() * 60);

            timeLine = new Timeline();
            timeLine.setCycleCount(count);

            KeyFrame kf = new KeyFrame(
                    javafx.util.Duration.seconds(1), new EventHandler<ActionEvent>() {
                        int i = 1;

                        @Override
                        public void handle(ActionEvent event) {
                            curseur.setTranslateX(pas * i);
                            i++;
                        }
                    });
            timeLine.getKeyFrames().add(kf);
            
        }

    }

    public static CursorTimeLineConduite getInstance(java.time.Duration duree, StackPane pane, Rectangle rect) {

        setDuration(duree);
        setPane(pane);
        setRectangle(rect);
        return TimeLineConduiteHolder.INSTANCE;

    }

    private static class TimeLineConduiteHolder {

        private static final CursorTimeLineConduite INSTANCE = new CursorTimeLineConduite();
    }

    private static Duration getDuration() {
        return duration;
    }

    private static void setDuration(Duration duration) {
        CursorTimeLineConduite.duration = duration;
    }

    private static Rectangle getRectangle() {
        return rectangle;
    }

    private static void setRectangle(Rectangle rectangle) {
        CursorTimeLineConduite.rectangle = rectangle;
    }

    public static StackPane getPane() {
        return pane;
    }

    private static void setPane(StackPane pane) {
        CursorTimeLineConduite.pane = pane;
    }

    public Timeline getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(Timeline timeLine) {
        this.timeLine = timeLine;
    }
    
    

}
