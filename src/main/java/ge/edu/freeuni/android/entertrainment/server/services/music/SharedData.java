package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.MusicHolder;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ge.edu.freeuni.android.entertrainment.server.services.music.MusicUtils.getDurationWithMagic;

public class SharedData {
    private static SharedData ourInstance = null;
    private  Date startDate;
    private byte[] data = new byte[50000];
    private int ratioInSec;
    private int pointer = 0 ;
    private int currentFileLengthInBytes;
    private int currentMusicIndex = 0;
    private MusicHolder musicHolder;
    private double currentSongDurationInSecs;
    private int identifier = 0 ;

    final Lock lock = new ReentrantLock();
    final Condition notEmpty = lock.newCondition();

    public static SharedData getInstance()
    {
        if(ourInstance == null){
            ourInstance = new SharedData();

        }
        return ourInstance;
    }

    private SharedData() {

    }
    public void init(){
        startDate = new Date();
        musicHolder = MusicHolder.getInstance();
        musicHolder.init();
        updateParameters();
    }

    private void updateParameters() {
        lock.lock();
        try {
            Music topMusic = getTopMusic();
            if (topMusic != null) {
                byte[] musicBytes = MusicDo.getMusicData(topMusic.getId());
                if (musicBytes != null) {
                    int fileLengthInBytes = musicBytes.length;
                    this.currentFileLengthInBytes = fileLengthInBytes;
                    this.data = musicBytes;
                    currentSongDurationInSecs = topMusic.getDuration();
                    ratioInSec = (int) (fileLengthInBytes / currentSongDurationInSecs);
                }
            }
            notEmpty.signalAll();
        }finally {
            lock.unlock();
        }
    }

    private Music getTopMusic() {
        List<Music> musics = musicHolder.getMusics();
        if (musics!= null && musics.size()!=0)
            return musics.get(0);
        return null;
    }


    public void  updateData(){
        this.pointer += this.ratioInSec;
        if (pointer >= this.currentFileLengthInBytes){
            musicHolder.newSong();
            pointer = 0 ;
            startDate = new Date();
            increaseIdentifier();
            updateParameters();
        }
    }

    public byte[] getData(){
        return  Arrays.copyOfRange(this.data, pointer, this.data.length);
    }









    public synchronized void increaseIdentifier(){
        identifier++ ;
        identifier = (int) (identifier% Math.pow(2,20));
    }

    public synchronized int getIdentifier(){
        return this.identifier;
    }

}
