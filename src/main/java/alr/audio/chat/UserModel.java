package alr.audio.chat;

import java.util.UUID;
import lombok.Getter;

@Getter
public class UserModel {

	private final String name;

	private final String id;

	public UserModel(String name) {
		this.name = name;
		this.id = UUID.randomUUID().toString();
	}

}
