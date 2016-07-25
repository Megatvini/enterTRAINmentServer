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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Consumes("application/json")
@Produces("application/json")
@Path("songs")
public class MusicService {

    @GET
    @Path("shared")
    public Response sharedMusic(@Context HttpServletRequest  req) throws CloneNotSupportedException {
        return playListData(req);
    }

    private Response playListData(@Context HttpServletRequest req) {
        String ip  = req.getRemoteAddr();
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
    public Response upVote(@PathParam("songId") String songId, @Context HttpServletRequest req) throws CloneNotSupportedException {
        String ip = req.getRemoteAddr();
        if(!MusicUtils.upvoted(ip,songId)) {
            MusicDo.vote(songId, 1, "up", ip);
        }
//        SharedMusicService.updateAll();

        return playListData(req);

    }



    @POST
    @Path("shared/{songId}/downvote")
    public Response downVote(@PathParam("songId") String songId, @Context HttpServletRequest req ) throws CloneNotSupportedException {
        String ip = req.getRemoteAddr();
        if  (!MusicUtils.downvoted(ip,  songId)) {
            MusicDo.vote(songId, -1, "down", ip);
        }
//        SharedMusicService.updateAll();
        return playListData(req);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public String upload(byte[] fileBytes) throws IOException {
        System.out.println(fileBytes.length);
        String tmp= "/tmp/temp.mp3";
        writeFile(fileBytes, tmp);
        String id = UUID.randomUUID().toString();
        String name = MusicUtils.getName(tmp);
        int duration = (int) MusicUtils.getDurationWithMagic(tmp);
        Music music = new Music(duration,id, name,0,"http://www.clipartbest.com/cliparts/dcr/ao9/dcrao9oxi.jpeg");
        MusicDo.saveMusic(music,fileBytes);
        Music music1 = MusicDo.getMusic(id);
        System.out.println(music1);
        MusicHolder.getInstance().init();
        SharedMusicService.updateAll();
        return id;
    }

    private void writeFile(byte[] fileBytes, String tmp) throws IOException {
        java.nio.file.Path file = Paths.get(tmp);
        Files.write(file, fileBytes);
    }


}
