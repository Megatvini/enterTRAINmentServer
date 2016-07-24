package ge.edu.freeuni.android.entertrainment.server.services.music.data;

import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.MusicUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MusicHolder {
    private static MusicHolder ourInstance = new MusicHolder();

    List<Music> musics;

    public static MusicHolder getInstance() {
        return ourInstance;
    }

    private MusicHolder( ) {
    }

    public void init(){
        musics = getLocalMusics();
    }

    private List<Music> getLocalMusics() {
        return MusicDo.getMusics();
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Music> musics) {
        this.musics = musics;
    }

    public void newSong() {
        if (musics != null && musics.size() >0) {
            MusicDo.resetRating(musics.get(0).getId());
            MusicUtils.sortMusics(musics);
        }
    }

    public Music findById(String id){
        for (Music music: musics){
            if (music.getId().equals(id))
                return music;
        }
        return null;
    }
}
