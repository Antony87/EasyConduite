
package easyconduite.objects.project;

import easyconduite.model.EasyMedia;
import easyconduite.objects.conduite.Conduite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette Classe comporte les attributs d'un projet EasyConduite.
 */
public class MediaProject {

    private String name;
    private Conduite conduite;
    List<EasyMedia> easyMediaList;

    public MediaProject() {
        this.conduite=new Conduite();
        this.easyMediaList = new ArrayList<EasyMedia>();
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

    public List<EasyMedia> getEasyMediaList() {
        return easyMediaList;
    }

    public void setEasyMediaList(List<EasyMedia> easyMediaList) {
        this.easyMediaList = easyMediaList;
    }
}
