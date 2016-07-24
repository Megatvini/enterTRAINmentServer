package ge.edu.freeuni.android.entertrainment.server.services.music;

import ge.edu.freeuni.android.entertrainment.server.services.GroupChatService;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;


@WebServlet(name = "SharedMusicServlet")
public class SharedMusicServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.register(SharedMusicService.class);
    }
}
