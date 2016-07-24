package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.SharedMusicSocketMessage;
import org.json.JSONObject;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MusicUtils {


    public static String getName(String fileName){
        File file = new File(fileName);
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



    public static ArrayList<Music> getResultMusics(String ip) throws CloneNotSupportedException {
        List<Music> musics = MusicDo.getMusics();
        ArrayList<Music> resultMusics = new ArrayList<>();
        if (musics != null) {
            for (Music music1 : musics){
                Music music2 = music1.clone();
                if (downvoted(ip,music1)) {
                    music2.setVoted("up");
                }else if(upvoted(ip,music1))
                    music2.setVoted("down");
                else
                    music2.setVoted("null");
                resultMusics.add(music2);
            }
        }
        return resultMusics;
    }

    public  static boolean upvoted(String id, Music music){
        return MusicDo.getVote(id,music.getId()).equals("up");
    }

    public  static boolean downvoted(String id, Music music){
        return MusicDo.getVote(id,music.getId()).equals("down");
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
}
