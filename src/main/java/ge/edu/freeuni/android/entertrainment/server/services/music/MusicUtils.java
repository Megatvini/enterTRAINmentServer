package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MusicUtils {




    public static double getDurationWithMagic(String fileName)  {

        double durationInSeconds = 0 ;
        try {
            File file = new File(fileName);
            AudioFileFormat fileFormat;
            fileFormat = AudioSystem.getAudioFileFormat(file);
            if (fileFormat instanceof TAudioFileFormat) {
                Map<?, ?> properties = fileFormat.properties();
                String key = "duration";
                Long microseconds = (Long) properties.get(key);
                int mili = (int) (microseconds / 1000);
                durationInSeconds = mili/1000;
            } else {
                throw new UnsupportedAudioFileException();
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }


        return durationInSeconds;

    }

    public static void sortMusics(List<Music> musics){
        Collections.sort(musics, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                return ((Integer) o1.getRating()).compareTo (o2.getRating());
            }
        });

    }
}
