package ge.edu.freeuni.android.entertrainment.server.services;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Created by Nika Doghonadze
 */
@SuppressWarnings("serial")
public class GroupChatServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory)
    {
        factory.register(GroupChatService.class);
    }
}
