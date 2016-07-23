package ge.edu.freeuni.android.entertrainment.server.services.music;


import ge.edu.freeuni.android.entertrainment.server.services.music.DO.MusicDo;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.MusicHolder;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Votes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Consumes("application/json")
@Produces("application/json")
@Path("songs")
public class MusicService {

    @GET
    @Path("shared")
    public Response sharedMusic(){
        Response build = Response.ok().entity(MusicHolder.getInstance().getMusics()).build();
        return build;
    }
    @GET
    @Path("offered")
    public Response offeredMusics(){
        Response build = Response.ok().entity(MusicHolder.getInstance().getMusics()).build();
        return build;
    }

    @POST
    @Path("shared/{songId}/upvote")
    public Response upVote(@PathParam("songId") String songId, @Context HttpServletRequest req) throws CloneNotSupportedException {
        System.out.println("upvote");
        MusicHolder instance = MusicHolder.getInstance();
        Music music = instance.findById(songId);
        if (music == null )
            music = instance.getMusics().get(0);
        String ip = req.getRemoteAddr();
        music.setRating(music.getRating()+1);
        List<Music> musics = Votes.getInstance().getUpvotes().get(ip);
        addVote(music, musics);
        ArrayList<Music> resultMusics = getResultMusics(ip);
        return Response.ok().entity(resultMusics).build();

    }

    private void addVote(Music music, List<Music> musics) {
        if (musics == null){
            musics = new ArrayList<>();
        }
        musics.add(music);
    }

    private ArrayList<Music> getResultMusics(String ip) throws CloneNotSupportedException {


        ArrayList<Music> musics = MusicHolder.getInstance().getMusics();
        ArrayList<Music> resultMusics = new ArrayList<>();
        for (Music music1 : musics){
            Music music2 = music1.clone();
            if (downvoted(ip,music1)) {
                music2.setVoted("up");
            }else if(upvoted(ip,music1))
                music2.setVoted("down");
            resultMusics.add(music2);
        }
        return resultMusics;
    }

    private boolean upvoted(String id, Music music){
        List<Music> id1 = Votes.getInstance().getUpvotes().get(id);
        if (id1!=null){
            for (Music music1: id1){
                if (music1.equals(music))
                    return true;
            }
        }
        return false;
    }

    private boolean downvoted(String id, Music music){
        List<Music> id1 = Votes.getInstance().getDownvotes().get(id);
        if (id1!=null){
            for (Music music1: id1){
                if (music1.equals(music))
                    return true;
            }
        }
        return false;
    }

    @POST
    @Path("{songId}/downvote")
    public Response downVote(@PathParam("songId") String songId, @Context HttpServletRequest req ) throws CloneNotSupportedException {
        Music music = MusicHolder.getInstance().findById(songId);
        String ip = req.getRemoteAddr();
        if (music.getRating() > 0){
            music.setRating(music.getRating()+1);
            List<Music> musics = Votes.getInstance().getDownvotes().get(ip);
            addVote(music, musics);
        }

        ArrayList<Music> resultMusics = getResultMusics(ip);
        return Response.ok().entity(resultMusics).build();


    }

    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public String upload(byte[] fileBytes) throws IOException {
        System.out.println(fileBytes.length);
        String id = UUID.randomUUID().toString();
        Music music = new Music(id,"random_name",0,"");
        MusicDo.saveMusic(music,fileBytes);
        Music music1 = MusicDo.getMusic(id);

        java.nio.file.Path file = Paths.get("/tmp/temp.mp3");
        Files.write(file, fileBytes);
        System.out.println(music1);
        return id;
    }


}
