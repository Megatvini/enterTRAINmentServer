package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.MusicHolder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Consumes("application/json")
@Produces("application/json")
@Path("songs")
public class MusicService {

    @GET
    @Path("shared")
    public Response sharedMusic(){
        List<Music> musics = MusicDo.getMusics();
        MusicUtils.sortMusics(musics);
        return Response.ok().entity(musics).build();
    }
    @GET
    @Path("offered")
    public Response offeredMusics(){
        List<Music> musics = MusicDo.getMusics();
        MusicUtils.sortMusics(musics);
        return Response.ok().entity(musics).build();
    }



    @POST
    @Path("shared/{songId}/upvote")
    public Response upVote(@PathParam("songId") String songId, @Context HttpServletRequest req) throws CloneNotSupportedException {
        System.out.println("upvote");
        String ip = req.getRemoteAddr();
        MusicDo.vote(songId,1,"up",ip);
        ArrayList<Music> resultMusics = getResultMusics(ip);
        MusicUtils.sortMusics(resultMusics);
        return Response.ok().entity(resultMusics).build();

    }


    private ArrayList<Music> getResultMusics(String ip) throws CloneNotSupportedException {
        List<Music> musics = MusicDo.getMusics();
        ArrayList<Music> resultMusics = new ArrayList<>();
        if (musics != null) {
            for (Music music1 : musics){
                Music music2 = music1.clone();
                if (downvoted(ip,music1)) {
                    music2.setVoted("up");
                }else if(upvoted(ip,music1))
                    music2.setVoted("down");
                else
                    music2.setVoted("null");
                resultMusics.add(music2);
            }
        }
        return resultMusics;
    }

    private boolean upvoted(String id, Music music){
        return MusicDo.getVote(id,music.getId()).equals("up");
    }

    private boolean downvoted(String id, Music music){
        return MusicDo.getVote(id,music.getId()).equals("down");
    }

    @POST
    @Path("{songId}/downvote")
    public Response downVote(@PathParam("songId") String songId, @Context HttpServletRequest req ) throws CloneNotSupportedException {
        String ip = req.getRemoteAddr();
        MusicDo.vote(songId,-1,"down",ip);
        ArrayList<Music> resultMusics = getResultMusics(ip);
        MusicUtils.sortMusics(resultMusics);
        return Response.ok().entity(resultMusics).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public String upload(byte[] fileBytes) throws IOException {
        System.out.println(fileBytes.length);
        String tmp= "/tmp/temp.mp3";
        writeFile(fileBytes, tmp);
        String id = UUID.randomUUID().toString();
        Music music = new Music((int) MusicUtils.getDurationWithMagic(tmp),id,"random_name",0,"");
        MusicDo.saveMusic(music,fileBytes);
        Music music1 = MusicDo.getMusic(id);
        System.out.println(music1);
        MusicHolder.getInstance().init();
        return id;
    }

    private void writeFile(byte[] fileBytes, String tmp) throws IOException {
        java.nio.file.Path file = Paths.get(tmp);
        Files.write(file, fileBytes);
    }


}
