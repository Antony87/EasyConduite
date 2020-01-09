package easyconduite.objects.project;

import easyconduite.model.EasyMedia;
import easyconduite.model.IeasyMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe représente une table sur laquelle sont présentes les pistes de Médias.
 */
public class MediaTable {

    List<EasyMedia> EasyMediaList;

    public MediaTable() {
        setEasyMediaList(new ArrayList<EasyMedia>());
    }

    public List<EasyMedia> getEasyMediaList() {
        return EasyMediaList;
    }

    public void setEasyMediaList(List<EasyMedia> easyMediaList) {
        EasyMediaList = easyMediaList;
    }
}
