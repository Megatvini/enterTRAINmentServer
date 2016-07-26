package ge.edu.freeuni.android.entertrainment.server.services.movie;


import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.MusicUtils;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ge.edu.freeuni.android.entertrainment.server.services.music.MusicService.defaultImagePath;
import static ge.edu.freeuni.android.entertrainment.server.services.music.MusicUtils.writeFile;

@Path("movies")
@Consumes("application/json")
@Produces("application/json")
public class MoviesService {

    @GET
    public Response moviesList(){
        List<Music> videos = MusicDo.getVideos();
        return Response.ok().entity(videos).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    public String addMovie(byte[] fileBytes,  @DefaultValue("unknown") @QueryParam("name") String videoName) throws IOException {

        String tmp = "/tmp/temp.mp4";
        writeFile(fileBytes, tmp);
        String id = UUID.randomUUID().toString();
        int duration = (int) MusicUtils.getDurationWithMagic(tmp);
        Music music = new Music(duration,id, videoName,0,defaultImagePath);
        MusicDo.saveMusic(music,fileBytes);
        MusicDo.saveVideo(music);
        return id;
    }
}
