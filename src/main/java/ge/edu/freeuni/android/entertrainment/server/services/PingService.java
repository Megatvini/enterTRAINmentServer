package ge.edu.freeuni.android.entertrainment.server.services;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("ping")
public class PingService {

    @GET
    public Response get() {
        System.out.println("get");
        return Response.ok().build();
    }

}
