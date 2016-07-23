package ge.edu.freeuni.android.entertrainment.server.model;

import java.util.List;

/**
 * Created by Nika Doghonadze
 */
public interface ChatRepository {
    List<ChatEntry> getAllEntries();
    void addEntry(ChatEntry chatEntry);
    void clearEntries();
    void addNewUser(String username);
    boolean userExists(String username);
}
