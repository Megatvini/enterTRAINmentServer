package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MusicUtils {

    public static ArrayList<Music> getLocalMusics(String directory){
        ArrayList<Music> musics = new ArrayList<>();
        String[] list = listDir(directory);
        String[] fullPaths = fileNames(directory);
        for (int i = 0; i < fullPaths.length; i++) {
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();
            Music music = new Music(id,list[i], 0, "", fullPaths[i]);
            musics.add(music);
        }
        return musics;

    }

    public static String[] fileNames(String directory){
        String[] list = listDir(directory);
        return concatDirectory(directory, list);
    }

    private static String[] listDir(String directory){
        File dir = new File(directory);
        return  dir.list();
    }

    private static String[] concatDirectory(String directory, String[] list) {
        String []fileNames = new String[list.length];
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i]= directory+"/"+list[i];
        }
        return fileNames;
    }

    public static double getDurationWithMagic(File file)  {

        double durationInSeconds = 0 ;
        try {
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
}
