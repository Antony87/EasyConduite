/*
 * Copyright (C) 2014 A Fons
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
package easyconduite.application;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import easyconduite.objects.AudioMedia;
import easyconduite.objects.AudioTable;
import java.io.File;
import java.util.List;

/**
 *
 * @author V902832
 */
public class PersistenceUtil {
    
    private String xml;
    
    public void save(File file,List<AudioMedia> listAudioMedia){
        
        AudioTable table = new AudioTable();
        table.setAudioMediaList(listAudioMedia);
        table.setName("toto");
        
        XStream xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(AudioMedia.class);
        xstream.processAnnotations(AudioTable.class);
        
        xml = xstream.toXML(table);
        
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
    
    
    
}
