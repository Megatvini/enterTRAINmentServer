package ge.edu.freeuni.android.entertrainment.server.services;

import ge.edu.freeuni.android.entertrainment.server.model.ChatRepository;
import ge.edu.freeuni.android.entertrainment.server.model.GroupChatRepository;
import ge.edu.freeuni.android.entertrainment.server.model.NameGenerator;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Nika Doghonadze
 */
@Path("chatname")
@Consumes( { MediaType.APPLICATION_JSON})
@Produces( { MediaType.APPLICATION_JSON})
public class ChatNameService {

    protected ChatRepository getRepository() {
        return GroupChatRepository.getInstance();
    }

    @GET
    public String get() {
        JSONObject jsonObject = new JSONObject();
        String randomName = getRandomName();
        jsonObject.put("username", randomName);
        return jsonObject.toString();
    }

    private String getRandomName() {
        ChatRepository repository = getRepository();
        String randomName = NameGenerator.generateRandomName();
        while (repository.userExists(randomName)) {
            randomName = NameGenerator.generateRandomName();
        }

        repository.addNewUser(randomName);
        return randomName;
    }

	@GET
	@Path("check/{username}")
	public Response post(@PathParam("username") String username) {
		ChatRepository repository = getRepository();
		if (repository.userExists(username)) {
			throw new NotFoundException();
		} else {
			repository.addNewUser(username);
			return Response.ok().build();
		}
	}
}
