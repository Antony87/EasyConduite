package easyconduite.fixture;

import easyconduite.model.AudioVisualMedia;
import easyconduite.objects.media.AudioVideoMedia;
import easyconduite.objects.media.MediaFactory;
import easyconduite.objects.project.MediaProject;

import java.io.File;

public class EasyProjectFixture {


    public static MediaProject getValidProject(){

        MediaProject project = new MediaProject();
        project.setName("valid project");
        AudioVisualMedia media = (AudioVideoMedia) MediaFactory.getAudioVisualMedia(new File("src/test/resources/Alarme.wav"));
        media.setVolume(50);
        media.setLoppable(true);
        project.getEasyMediaList().add(media);

        return project;
    }
}
