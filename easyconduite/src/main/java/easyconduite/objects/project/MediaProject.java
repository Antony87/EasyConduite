
package easyconduite.objects.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import easyconduite.model.AbstractMedia;
import easyconduite.objects.conduite.Conduite;

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
    private List<AbstractMedia> abstractMediaList;

    @JsonIgnore
    private boolean needToSave =false;

    public MediaProject() {
        this.conduite = new Conduite();
        this.abstractMediaList = new ArrayList<AbstractMedia>(1);
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

    public List<AbstractMedia> getAbstractMediaList() {
        return abstractMediaList;
    }

    public void setAbstractMediaList(List<AbstractMedia> abstractMediaList) {
        this.abstractMediaList = abstractMediaList;
    }

    public boolean isNeedToSave() {
        return needToSave;
    }

    public void setNeedToSave(boolean needToSave) {
        this.needToSave = needToSave;
    }
}
