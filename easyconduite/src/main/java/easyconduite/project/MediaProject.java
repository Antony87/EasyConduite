
/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package easyconduite.project;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import easyconduite.conduite.Conduite;
import easyconduite.model.MediaPlayable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette Classe comporte les attributs d'un projet EasyConduite.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeName("MediaProject")
public class MediaProject {

    private String name;

    private Conduite conduite;

    private Path projectPath;

    private List<MediaPlayable> mediaPlayables;

    public MediaProject() {
        this.conduite = new Conduite();
        this.mediaPlayables = new ArrayList<>(100);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Conduite getConduite() {
        return conduite;
    }

    public void setConduite(Conduite conduite) {
        this.conduite = conduite;
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(Path projectPath) {
        this.projectPath = projectPath;
    }

    public List<MediaPlayable> getMediaPlayables() {
        return mediaPlayables;
    }

    public void setMediaPlayables(List<MediaPlayable> mediaPlayables) {
        this.mediaPlayables = mediaPlayables;
    }
}
