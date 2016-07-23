package ge.edu.freeuni.android.entertrainment.server.services;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.*;

/**
 * Created by Nika Doghonadze
 */
public class RandomChatService extends WebSocketAdapter {
    private static Map<Session, Session> sessionPairs =
            Collections.synchronizedMap(new HashMap<Session, Session>());

    private static Set<Session> unpairedSessions =
            Collections.synchronizedSet(new HashSet<Session>());

    private static String PAIR_FOUND_CODE = "0b83465e-000b-4361-901f-e83840c6a29f";

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);

        if (unpairedSessions.isEmpty()) {
            unpairedSessions.add(sess);
        } else {
            Session randomSession = getRandomUnpaired();
            while (randomSession != null && !randomSession.isOpen()) {
                unpairedSessions.remove(randomSession);
                randomSession = getRandomUnpaired();
            }
            unpairedSessions.remove(randomSession);
            sessionPairs.put(sess, randomSession);
            sessionPairs.put(randomSession, sess);

            sendFoundMessage(sess);
            sendFoundMessage(randomSession);

        }
        System.out.println("Socket Connected: " + sess);
    }

    private void sendFoundMessage(Session session) {
        try {
            session.getRemote().sendString(PAIR_FOUND_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Session getRandomUnpaired() {
        int size = unpairedSessions.size();
        int item = new Random().nextInt(size);
        int i = 0;
        for (Session session : unpairedSessions) {
            if (i == item)
                return session;
            i = i + 1;
        }
        return null;
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);

        Session session = getSession();
        Session pair = sessionPairs.get(session);
        sendTextSafe(pair, message);
    }

    private void sendTextSafe(Session session, String message) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            session.close();
            sessionPairs.remove(session).close();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        notifyPair();
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        notifyPair();
        cause.printStackTrace(System.err);
    }

    private void notifyPair() {
        Session session = getSession();
        unpairedSessions.remove(session);
        if (sessionPairs.containsKey(session)) {
            Session sessionPair = sessionPairs.remove(session);
            sessionPair.close();
            sessionPairs.remove(sessionPair);
        }
        sessionPairs.remove(session);
    }
}