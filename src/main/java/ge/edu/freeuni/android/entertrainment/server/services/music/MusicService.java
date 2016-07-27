package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.MusicHolder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static ge.edu.freeuni.android.entertrainment.server.services.music.MusicUtils.writeFile;

@Consumes("application/json")
@Produces("application/json")
@Path("songs")
public class MusicService {
    public static final  String defaultImagePath = "http://www.clipartbest.com/cliparts/dcr/ao9/dcrao9oxi.jpeg";

    @GET
    @Path("shared")
    public Response sharedMusic(@Context HttpServletRequest  req, @DefaultValue("unknown") @QueryParam("id") String id) throws CloneNotSupportedException {
        return playListData(id);
    }

    private Response playListData( String ip) {
        List<Music> resultMusics = MusicUtils.getResultMusics(ip);
        MusicUtils.sortMusics(resultMusics);
        return Response.ok().entity(resultMusics).build();
    }

    @GET
    @Path("offered")
    public Response offeredMusics(){
        List<Music> musics = MusicDo.getMusics();
        return Response.ok().entity(musics).build();
    }



    @POST
    @Path("shared/{songId}/upvote")
    public Response upVote(@PathParam("songId") String songId,
                           @DefaultValue("unknown") @QueryParam("id") String id) throws CloneNotSupportedException {
        if(!MusicUtils.upvoted(id,songId)) {
            MusicDo.vote(songId, 1, "up", id);
        }
        SharedMusicService.updateAll();
        return Response.ok().build();

    }



    @POST
    @Path("shared/{songId}/downvote")
    public Response downVote(@PathParam("songId") String songId,
                             @DefaultValue("unknown") @QueryParam("id") String id
    ) throws CloneNotSupportedException {
        if  (!MusicUtils.downvoted(id,  songId)) {
            if(notZero(songId))
                MusicDo.vote(songId, -1, "down", id);
        }
        SharedMusicService.updateAll();
        return   Response.ok().build();
    }

    private boolean notZero(String songId) {
        Music music = MusicDo.getMusic(songId);
        return music != null && music.getRating() != 0;
    }


    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public String upload(byte[] fileBytes,  @DefaultValue("unknown") @QueryParam("name") String songName) throws IOException {
        String tmp= "/tmp/temp.mp3";
        writeFile(fileBytes, tmp);
        String id = UUID.randomUUID().toString();
        int duration = (int) MusicUtils.getDurationWithMagic(tmp);
        Music music = new Music(duration,id, songName,0,defaultImagePath);
        MusicDo.saveMusic(music,fileBytes);
        MusicDo.saveAudio(music);
        MusicHolder.getInstance().init();
        SharedMusicService.updateAll();
        return id;
    }




}
