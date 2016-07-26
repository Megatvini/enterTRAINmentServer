package ge.edu.freeuni.android.entertrainment.server.services;

import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Arrays;

/**
 * Created by Nika Doghonadze
 */
@Path("mediastream")
public class VideoStreamingService {
    @GET
    @Path("audio/{filename}")
    @Produces("audio/mp3")
    public Response streamAudio(@HeaderParam("Range") String range,
                                @PathParam("filename")  String filename) throws Exception {


        return buildStream(filename, range);
    }
    @GET
    @Path("video/{filename}")
    @Produces("video/mp4")
    public Response streamVideo(@HeaderParam("Range") String range,
                                @PathParam("filename")  String filename) throws Exception {


        return buildStream(filename, range);
    }

    private Response buildStream(final String  fileName, final String range) throws Exception {
        byte[] musicData = MusicDo.getMusicData(fileName);
        if (musicData == null){
            return Response.serverError().build();
        }
        if (range == null) {
            return Response.ok(musicData).status(200).header(HttpHeaders.CONTENT_LENGTH, musicData.length).header("Accept-Ranges","bytes").build();
        }

        String[] ranges = range.split("=")[1].split("-");
        final int from = Integer.parseInt(ranges[0]);
        int chunk_size = 204800000;

        int to = chunk_size + from;
        if (to >= musicData.length) {
            to = musicData.length - 1;
        }
        if (ranges.length == 2) {
            to = Integer.parseInt(ranges[1]);
        }

        final String responseRange = String.format("bytes %d-%d/%d", from, to, musicData.length);
        byte[] bytes = Arrays.copyOfRange(musicData,from,to);

        Response.ResponseBuilder res = Response.ok(bytes).status(206)
                .header("Accept-Ranges", "bytes")
                .header("Content-Range", responseRange)
                .header(HttpHeaders.CONTENT_LENGTH, bytes.length);
        return res.build();
    }
}
