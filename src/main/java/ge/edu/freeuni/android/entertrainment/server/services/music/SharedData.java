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
    private MusicDo musicDo;

    public static SharedData getInstance()
    {
        if(ourInstance == null){
            ourInstance = new SharedData();

        }
        return ourInstance;
    }

    private SharedData() {

    }
    public void init(String directory, MusicDo musicDo){
        this.musicDo = musicDo;
        startDate = new Date();
        musicHolder = MusicHolder.getInstance();
        musicHolder.init(directory);
        updateParameters();
    }

    private void updateParameters() {
        lock.lock();
        try {
            Music topMusic = getTopMusic();
            if (topMusic != null) {
                byte[] musicBytes;
                String fileName = topMusic.getFilePath();
                File iniFile = new File(fileName);
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
                    long fileLengthInBytes = randomAccessFile.length();
                    this.currentFileLengthInBytes = (int) fileLengthInBytes;
                    byte[] tempBytes = new byte[(int) fileLengthInBytes];
                    this.data = tempBytes;
                    randomAccessFile.readFully(tempBytes);
                    currentSongDurationInSecs = getDurationWithMagic(iniFile);
                    ratioInSec = (int) (fileLengthInBytes / currentSongDurationInSecs);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            notEmpty.signalAll();
        }finally {
            lock.unlock();
        }
    }

    private Music getTopMusic() {
        return musicHolder.getMusics().get(0);
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
