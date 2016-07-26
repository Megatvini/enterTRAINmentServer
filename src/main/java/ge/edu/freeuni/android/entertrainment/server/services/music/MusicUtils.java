package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.Utils;
import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.SharedMusicSocketMessage;
import org.json.JSONObject;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MusicUtils {


    public static String getName(String fileName){
        File file = new File(fileName);
        return nameFromFile(file);
    }

    public static String nameFromFile(File file) {
        AudioFileFormat fileFormat;
        try {
            fileFormat = AudioSystem.getAudioFileFormat(file);
            if (fileFormat instanceof TAudioFileFormat) {
                Map<?, ?> properties = fileFormat.properties();
                String author = (String) properties.get("author");
                String name = (String) properties.get("title");
                return String.format("%s - %s", author,name);
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

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
                return ((Integer) o2.getRating()).compareTo (o1.getRating());
            }
        });

    }

    public static SharedMusicSocketMessage fromString(String data){
        JSONObject jsonObject = new JSONObject(data);
        return new SharedMusicSocketMessage(jsonObject.getString("music_id"),jsonObject.getInt("vote"));
    }



    public static List<Music> getResultMusics(String ip)  {
        List<Music> musics = MusicDo.getMusics();
        if (musics != null) {
            for (Music music : musics){
                if (downvoted(ip,music.getId())) {
                    music.setVoted("down");
                }else if(upvoted(ip,music.getId()))
                    music.setVoted("up");
                else
                    music.setVoted("null");

            }
        }
        return musics;
    }

    public  static boolean upvoted(String ip, String  musicId){
        return MusicDo.getVote(ip,musicId).equals("up");
    }

    public  static boolean downvoted(String ip, String musicId){
        return MusicDo.getVote(ip,musicId).equals("down");
    }


    public static String fromMusicArray(List<Music> musics){
        String s = "[" ;
                s += musics.get(0).toJson();
        for (int i = 1; i < musics.size(); i++) {
                s+=", "+musics.get(i).toJson();
        }
                s+="]";
        return s;
    }

    public static File[] filesList(String dir){
        File fileFromResources = Utils.getFileFromResources(dir);

        if (fileFromResources != null) {
           return fileFromResources.listFiles();
        }
        return new File[0];
    }

    public static void writeFile(byte[] fileBytes, String tmp) throws IOException {
        java.nio.file.Path file = Paths.get(tmp);
        Files.write(file, fileBytes);
    }
}
