package ge.edu.freeuni.android.entertrainment.server.services.music.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Votes {
    private static Votes ourInstance = new Votes();

    public static Votes getInstance() {
        return ourInstance;
    }

    private Votes() {
        upvotes = new HashMap<>();
        downvotes = new HashMap<>();
    }

    private Map<String , List<Music>> upvotes;
    private Map<String , List<Music>> downvotes;

    public Map<String, List<Music>> getUpvotes() {
        return upvotes;
    }

    public Map<String, List<Music>> getDownvotes() {
        return downvotes;
    }
}
