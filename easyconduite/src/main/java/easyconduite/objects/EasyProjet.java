
package easyconduite.objects;

import easyconduite.model.EasyMediaClass;

import java.util.List;

/**
 * Cette Classe comporte les attributs d'un projet EasyConduite.
 */
public class EasyProjet {

    private String name;
    private EasyTable table;
    private Conduite conduite;
    private List<EasyMediaClass> medias;

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
}
