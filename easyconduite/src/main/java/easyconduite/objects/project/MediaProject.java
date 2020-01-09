
package easyconduite.objects.project;

import easyconduite.objects.conduite.Conduite;

import java.io.File;

/**
 * Cette Classe comporte les attributs d'un projet EasyConduite.
 */
public class MediaProject {

    private String name;
    private EasyTable table;
    private Conduite conduite;
    private File projectFile;

    public MediaProject() {
        this.table=new EasyTable();
        this.conduite=new Conduite();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EasyTable getTable() {
        return table;
    }

    public void setTable(EasyTable table) {
        this.table = table;
    }

    public Conduite getConduite() {
        return conduite;
    }

    public void setConduite(Conduite conduite) {
        this.conduite = conduite;
    }

    public File getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }
}
