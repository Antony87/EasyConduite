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
package easyconduite.util;

import easyconduite.ui.EasyconduitePlayer;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 *
 * @author antony
 */
public class EasyFadeTransition extends Transition {

    private final EasyconduitePlayer easyPlayer;
    
    public EasyFadeTransition(EasyconduitePlayer player) {
        super();
        easyPlayer = player;
    }

    public void fadeOut(Duration fadeDuration) {
        this.setRate(-1d);
        this.setCycleDuration(fadeDuration);
        this.jumpTo(getCycleDuration());
        this.setOnFinished(getFadeOutHandler());
        this.play();
    }

    public void fadeIn(Duration fadeDuration) {
        easyPlayer.getPlayer().setVolume(0d);
        this.setRate(1d);
        this.setCycleDuration(fadeDuration);
        this.setOnFinished(getFadeInHandler());
        this.play();
    }

    private EventHandler<ActionEvent> getFadeOutHandler() {
        return (ActionEvent event) -> {
            easyPlayer.getPlayer().pause();
            setOnFinished(null);
        };
    }

    private EventHandler<ActionEvent> getFadeInHandler() {
        return (ActionEvent event) -> {
            easyPlayer.getPlayer().setVolume(easyPlayer.getInitialVolume());
            setOnFinished(null);
        };
    }

    @Override
    protected void interpolate(double frac) {
        easyPlayer.getPlayer().setVolume(frac * easyPlayer.getInitialVolume());
        System.out.println(frac);
    }

}
