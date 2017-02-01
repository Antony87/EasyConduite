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
public class AudioMediaUpdater {

    private Duration audioDuration;

    private Duration fadeInDuration;

    private Duration fadeOutDuration;

    private Boolean repeatable;

    private Double volume;

    private String name;

    private KeyCode keycode;

    public static class Builder {

        private Duration audioDuration;

        private Duration fadeInDuration;

        private Duration fadeOutDuration;

        private Boolean repeatable;

        private Double volume;

        private String name;

        private KeyCode keycode;

        public Builder() {
        }

        public Builder withDuration(Duration duration) {
            this.audioDuration = duration;
            return this;
        }

        public Builder withfadeIn(Duration duration) {
            this.fadeInDuration = duration;
            return this;
        }

        public Builder withfadeOut(Duration duration) {
            this.fadeOutDuration = duration;
            return this;
        }

        public Builder withRepeat(Boolean repeat) {
            this.repeatable = repeat;
            return this;
        }

        public Builder withVolume(Double volume) {
            this.volume = volume;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withKeyCode(KeyCode code) {
            this.keycode = code;
            return this;
        }

        public AudioMediaUpdater build() {
            AudioMediaUpdater updater = new AudioMediaUpdater();
            updater.audioDuration = this.audioDuration;
            updater.fadeInDuration = this.fadeInDuration;
            updater.fadeOutDuration = this.fadeOutDuration;
            updater.keycode = this.keycode;
            updater.name = this.name;
            updater.repeatable = this.repeatable;
            updater.volume = this.volume;
            return updater;
        }
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
        if (!Objects.equals(this.repeatable, null)) {
            media.setRepeatable(this.repeatable);
        }
        if (!Objects.equals(this.volume, null)) {
            media.setVolume(this.volume);
        }
        if (!Objects.equals(this.volume, null)) {
            media.setName(this.name);
        }
        if (!Objects.equals(this.keycode, null)) {
            media.setKeycode(this.keycode);
        }
    }

    private AudioMediaUpdater() {
    }

}
