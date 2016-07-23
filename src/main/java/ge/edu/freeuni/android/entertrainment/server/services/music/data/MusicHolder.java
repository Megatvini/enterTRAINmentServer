package ge.edu.freeuni.android.entertrainment.server.services.music.data;

import ge.edu.freeuni.android.entertrainment.server.services.music.MusicUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MusicHolder {
    private static MusicHolder ourInstance = new MusicHolder();

    ArrayList<Music> musics;

    public static MusicHolder getInstance() {
        return ourInstance;
    }

    private MusicHolder( ) {


    }

    public void init(String directory){
        musics = MusicUtils.getLocalMusics(directory);
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Music> musics) {
        this.musics = musics;
    }

    public void newSong() {
        musics.remove(0);
        Collections.sort(musics, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public Music findById(String id){
        for (Music music: musics){
            if (music.getId().equals(id))
                return music;
        }
        return null;
    }
}
