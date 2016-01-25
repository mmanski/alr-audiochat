package alr.audio.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class AudioChatController {

	@Autowired
	private UserHandlerService userHandlerService;

	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping("/users/{username}")
	public UserModel registerUser(@DestinationVariable String username) {
		UserModel userModel = new UserModel(username);
		userHandlerService.addUser(userModel);
		template.convertAndSend("/topic/broadcastQueue", userHandlerService.getBrodcasters());
		template.convertAndSend("/topic/broadcaster", userHandlerService.getBroadcaster());
		template.convertAndSend("/topic/listeners", userHandlerService.getUsers());
		return userModel;
	}

	@MessageMapping("/users/broadcast/{user}")
	public String broadcastRequest(@DestinationVariable String user) {
		UserModel userModel = userHandlerService.getUsers().stream()
				.filter(
						element -> element.getName().equals(user)
				).findFirst().get();
		userHandlerService.addBroadcaster(userModel);
		template.convertAndSend("/topic/broadcastQueue", userHandlerService.getBrodcasters());
		template.convertAndSend("/topic/broadcaster", userHandlerService.getBroadcaster());
		template.convertAndSend("/topic/listeners", userHandlerService.getUsers());
		return "Added";
	}

	@MessageMapping("/users/broadcast/remove/{user}")
	public String broadcastRemove(@DestinationVariable String user) {
		UserModel userModel = userHandlerService.getBrodcasters().stream()
				.filter(element -> element.getName().equals(user))
				.findFirst()
				.orElse(userHandlerService.getBroadcaster());
		userHandlerService.removeBroadcaster(userModel);
		template.convertAndSend("/topic/broadcastQueue", userHandlerService.getBrodcasters());
		template.convertAndSend("/topic/broadcaster", userHandlerService.getBroadcaster());
		template.convertAndSend("/topic/listeners", userHandlerService.getUsers());
		return "Removed";
	}

}
