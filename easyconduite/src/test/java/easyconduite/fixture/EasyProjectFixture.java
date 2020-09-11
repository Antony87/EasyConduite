package easyconduite.fixture;

import easyconduite.model.AbstractMedia;
import easyconduite.media.AudioMedia;
import easyconduite.media.MediaFactory;
import easyconduite.project.MediaProject;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.io.File;

public class EasyProjectFixture {


    public static MediaProject getValidProject(){

        MediaProject project = new MediaProject();
        project.setName("valid project");
        AbstractMedia media = MediaFactory.createPlayableMedia(new File("C:\\Users\\V902832\\IdeaProjects\\EasyConduite\\easyconduite\\src\\test\\resources\\Alarme.wav"));
        media.setDuration(new Duration(1000));
        ((AudioMedia)media).setVolume(0.50);
        media.setLoppable(true);
        media.setKeycode(KeyCode.A);
        project.getMediaPlayables().add(media);

        return project;
    }
}
