package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.SharedMusicSocketMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class SharedMusicService  extends WebSocketAdapter{

    public static Set<Session> sessionSet =
            Collections.synchronizedSet(new HashSet<Session>());


    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        super.onWebSocketBinary(payload, offset, len);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        sessionSet.remove(getSession());
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        sessionSet.add(sess);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        SharedMusicSocketMessage socketMessage = MusicUtils.fromString(message);
        String hostString = getSession().getRemoteAddress().getHostString();
        System.out.println(hostString);
        if (socketMessage.getVote() == -1 ){
            MusicDo.vote(socketMessage.getMusic_id(),-1,"down", hostString);

        }else if (socketMessage.getVote() == -1 ){
            MusicDo.vote(socketMessage.getMusic_id(),1,"up", hostString);

        }
        updateAll();

    }

    public static void updateAll() {
//        List<Music> musics = MusicDo.getMusics();
//        MusicUtils.sortMusics(musics);
//        String s = MusicUtils.fromMusicArray(musics);
        for (Session session: sessionSet){
            if (session.isOpen()){
                try {
                    session.getRemote().sendString("update");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
