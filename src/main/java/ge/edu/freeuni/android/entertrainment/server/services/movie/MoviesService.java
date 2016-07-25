package ge.edu.freeuni.android.entertrainment.server.services.movie;


import ge.edu.freeuni.android.entertrainment.server.services.music.MusicUtils;
import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ge.edu.freeuni.android.entertrainment.server.services.music.MusicService.defaultImagePath;

@Path("movies")
@Consumes("application/json")
@Produces("application/json")
public class MoviesService {

    @GET
    public Response moviesList(){
        File[] files = MusicUtils.filesList("video");
        List<Music> musics = new ArrayList<>();
        for (File file: files){
            String name1 = file.getName();
            Music music = new Music(0,name1,name1,0,defaultImagePath);
            musics.add(music);
        }
        return Response.ok().entity(musics).build();
    }
}
