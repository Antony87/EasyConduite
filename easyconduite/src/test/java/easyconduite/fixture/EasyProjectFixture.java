package easyconduite.fixture;

import easyconduite.model.EasyMedia;
import easyconduite.objects.media.AudioMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.MediaProject;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.io.File;

public class EasyProjectFixture {


    public static MediaProject getValidProject(){

        MediaProject project = new MediaProject();
        project.setName("valid project");
        EasyMedia media = MediaFactory.getPlayableMedia(new File("C:\\Users\\V902832\\IdeaProjects\\EasyConduite\\easyconduite\\src\\test\\resources\\Alarme.wav"));
        media.setDuration(new Duration(1000));
        ((AudioMedia)media).setVolume(0.50);
        media.setLoppable(true);
        media.setKeycode(KeyCode.A);
        project.getEasyMediaList().add(media);

        return project;
    }
}
