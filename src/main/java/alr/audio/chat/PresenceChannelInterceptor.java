package alr.audio.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

/**
 *
 * @author rasgrass
 */
public class PresenceChannelInterceptor extends ChannelInterceptorAdapter {

	@Autowired
	private UserHandlerService userHandlerService;

	@Autowired
	@Lazy
	private SimpMessagingTemplate template;

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);

		// ignore non-STOMP messages like heartbeat messages
		if (sha.getCommand() == null) {
			return;
		}

		String sessionId = sha.getSessionId();

		switch (sha.getCommand()) {
			case DISCONNECT:
				UserModel user = userHandlerService.getListeningUsers().stream()
						.filter(element -> element.getId().equals(sessionId)).findFirst().orElse(null);
				if (user != null) {
					userHandlerService.removeUser(user);
				} else {
					user = userHandlerService.getBrodcasters().stream()
							.filter(element -> element.getId().equals(sessionId))
							.findFirst()
							.orElse(userHandlerService.getBroadcaster());
					userHandlerService.removeBroadcasterPermanently(user);
				}
				template.convertAndSend("/topic/broadcastQueue", userHandlerService.getBrodcasters());
				template.convertAndSend("/topic/broadcaster", userHandlerService.getBroadcaster());
				template.convertAndSend("/topic/listeners", userHandlerService.getListeningUsers());
				break;
			default:
				break;
		}
	}

}
