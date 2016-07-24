package ge.edu.freeuni.android.entertrainment.server.services;

import ge.edu.freeuni.android.entertrainment.server.model.ChatEntry;
import ge.edu.freeuni.android.entertrainment.server.model.GroupChatRepository;
import ge.edu.freeuni.android.entertrainment.server.model.ChatRepository;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.*;

/**
 * Created by Nika Doghonadze
 */
public class GroupChatService extends WebSocketAdapter {
    private static Set<Session> sessionSet =
            Collections.synchronizedSet(new HashSet<Session>());


    protected ChatRepository getRepository() {
        return GroupChatRepository.getInstance();
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        sessionSet.add(sess);

        ChatRepository chatRepository = getRepository();
        List<ChatEntry> allEntries = chatRepository.getAllEntries();

        if (allEntries.isEmpty()) {
            try {
                sess.getRemote().sendString("no data");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (ChatEntry entry : allEntries) {
            try {
                sess.getRemote().sendString(entry.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);

        ChatRepository chatRepository = getRepository();
        ChatEntry chatEntry = ChatEntry.fromJson(message);
        chatRepository.addEntry(chatEntry);

        for (Session userSession: sessionSet) {
            try {
                if (userSession.isOpen()) {
                    userSession.getRemote().sendString(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Received TEXT message: " + message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
        sessionSet.remove(getSession());
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
        sessionSet.remove(getSession());
    }
}
