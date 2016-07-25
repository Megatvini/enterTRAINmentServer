package ge.edu.freeuni.android.entertrainment.server.services.music.data;


public class SharedMusicSocketMessage {

    String music_id;
    int vote;

    public SharedMusicSocketMessage(String music_id, int vote) {
        this.music_id = music_id;
        this.vote = vote;
    }

    public String getMusic_id() {
        return music_id;
    }

    public void setMusic_id(String music_id) {
        this.music_id = music_id;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
