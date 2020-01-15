package easyconduite.objects;

import com.thoughtworks.xstream.XStream;
import static org.junit.jupiter.api.Assertions.*;

import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EasyConduitePropertiesTest {

    private EasyConduiteProperties props;

    @BeforeEach
    public void setup(){
        props = new EasyConduiteProperties();
    }

    @Test
    public void testChangeListener(){
        props.setWindowWith(200);
        props.setWindowHeight(300);
    }

    @Test
    public void testSerialize(){
        XStream xstream = new XStream(new DomDriver());
        props.setWindowWith(200);
        props.setWindowHeight(300);
        final String xmlProps = xstream.toXML(props);
        System.out.println(xmlProps);

        EasyConduiteProperties props2 = (EasyConduiteProperties) xstream.fromXML(xmlProps);
        assertEquals(200,props2.getWindowWith());
        assertEquals(300,props2.getWindowHeight());
        props2.setWindowWith(400);
    }

}