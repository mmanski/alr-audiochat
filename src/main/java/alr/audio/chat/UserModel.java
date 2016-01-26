package alr.audio.chat;

import lombok.Getter;

@Getter
public class UserModel {

	private final String name;

	private final String id;

	public UserModel(String name, String sessionId) {
		this.name = name;
		this.id =sessionId;
	}

}
