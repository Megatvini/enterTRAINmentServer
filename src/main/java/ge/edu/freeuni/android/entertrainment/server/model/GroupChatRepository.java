package ge.edu.freeuni.android.entertrainment.server.model;

import java.util.*;

/**
 * Created by Nika Doghonadze
 */
public class GroupChatRepository implements ChatRepository {
    private static GroupChatRepository instance;

    private Set<String> users;
    private Set<ChatEntry> chatEntryTreeSet;

    public GroupChatRepository(Set<String> users, Set<ChatEntry> chatEntryTreeSet) {
        this.users = users;
        this.chatEntryTreeSet = chatEntryTreeSet;
    }

    public List<ChatEntry> getAllEntries() {
        List<ChatEntry> res = new ArrayList<>();
        res.addAll(chatEntryTreeSet);
        return res;
    }


    public void addEntry(ChatEntry chatEntry) {
        chatEntry.setTimestamp(System.currentTimeMillis());
        chatEntryTreeSet.add(chatEntry);
    }

    public void clearEntries() {
        chatEntryTreeSet.clear();
    }

    @Override
    public void addNewUser(String username) {
        users.add(username);
    }

    @Override
    public boolean userExists(String username) {
        return users.contains(username);
    }

    @Override
    public void replaceUsername(String oldName, String username) {
        for (ChatEntry chatEntry : chatEntryTreeSet) {
            if (chatEntry.getUsername().equals(oldName)) {
                chatEntry.setUsername(username);
            }
        }
        users.remove(oldName);
        users.add(username);
    }

    public static synchronized GroupChatRepository getInstance() {
        if (instance == null) {
            Set<ChatEntry> chatEntryTreeSet = Collections.synchronizedSet(new TreeSet<>(new Comparator<ChatEntry>() {
                @Override
                public int compare(ChatEntry o1, ChatEntry o2) {
                    return Long.compare(o1.getTimestamp(), o2.getTimestamp());
                }
            }));

            Set<String> users = new TreeSet<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });

            instance = new GroupChatRepository(users, chatEntryTreeSet);
        }

        return instance;
    }

}
