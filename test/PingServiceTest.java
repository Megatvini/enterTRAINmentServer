import ge.edu.freeuni.android.entertrainment.server.services.PingService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nika Doghonadze.
 */
public class PingServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(PingService.class);
    }

    @Test
    public void testGetPing() {
        Response actual = target("ping").request().get();
        assertEquals(Response.Status.OK.getStatusCode(), actual.getStatus());
    }
}
