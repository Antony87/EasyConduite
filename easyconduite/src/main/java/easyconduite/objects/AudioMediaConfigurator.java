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
package easyconduite.objects;

import java.util.Objects;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * Class provides a builder pattern's for update an AudioMedia.
 *
 * @author antony
 */
public class AudioMediaConfigurator {

    private Duration audioDuration;

    private Duration fadeInDuration;

    private Duration fadeOutDuration;

    private Boolean repeatable;

    private String name;

    private KeyCode keycode;

    private boolean keychanged;
    
    public AudioMediaConfigurator withDuration(Duration duration) {
        this.audioDuration = duration;
        return this;
    }

    public AudioMediaConfigurator withfadeIn(Duration duration) {
        this.fadeInDuration = duration;
        return this;
    }

    public AudioMediaConfigurator withfadeOut(Duration duration) {
        this.fadeOutDuration = duration;
        return this;
    }

    public AudioMediaConfigurator withRepeat(Boolean repeat) {
        this.repeatable = repeat;
        return this;
    }

    public AudioMediaConfigurator withName(String name) {
        this.name = name;
        return this;
    }

    public AudioMediaConfigurator withKeyCodeChanged(KeyCode code) {
        this.keychanged = true;
        this.keycode = code;
        return this;
    }

    public void update(AudioMedia media) {
        if (this.audioDuration != null) {
            media.setAudioDuration(this.audioDuration);
        }
        if (this.fadeInDuration != null) {
            media.setFadeInDuration(this.fadeInDuration);
        }
        if (this.fadeOutDuration != null) {
            media.setFadeInDuration(this.fadeOutDuration);
        }
        if (this.repeatable != null) {
            media.setRepeatable(this.repeatable);
        }
        if (!Objects.equals(this.name, null)) {
            media.setName(this.name);
        }
        if (keychanged) {
            media.setKeycode(this.keycode);
        }
    }

}
