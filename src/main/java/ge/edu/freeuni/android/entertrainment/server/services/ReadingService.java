package ge.edu.freeuni.android.entertrainment.server.services;

import ge.edu.freeuni.android.entertrainment.server.Utils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * Created by Nika Doghonadze
 */
@Path("reading")
public class ReadingService {
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{filename}")
    public Response get(@PathParam("filename") String filename) {
        File file = Utils.getFileFromResources(filename);
        if (file == null)
            throw new NotFoundException();

        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" )
                .build();
    }
}
