package alr.audio.chat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

@Service
public class UserHandlerService {

	private volatile UserModel broadcaster;

	private final List<UserModel> listeningUsers;

	private final Queue<UserModel> broadcastQueue;

	public UserHandlerService() {
		this.listeningUsers = new CopyOnWriteArrayList<>();
		this.broadcastQueue = new ArrayBlockingQueue<>(50);
	}

	public void addUser(UserModel user) {
		if (listeningUsers.isEmpty() && broadcaster == null) {
			broadcaster = user;
		} else {
			listeningUsers.add(user);
		}
	}

	public void removeUser(UserModel user) {
		listeningUsers.remove(user);

	}

	public void addBroadcaster(UserModel user) {
		if (broadcastQueue.isEmpty() && broadcaster == null) {
			broadcaster = user;
		} else {
			broadcastQueue.add(user);
		}
	}

	public void removeBroadcaster(UserModel user) {
		if (broadcastQueue.contains(user)) {
			broadcastQueue.remove(user);
		} else {
			if (user.getId().equals(broadcaster.getId())) {
				synchronized (this) {
					broadcaster = broadcastQueue.poll();
					listeningUsers.add(user);
				}
			}
		}

	}

	public Collection<UserModel> getBrodcasters() {
		return Collections.unmodifiableCollection(broadcastQueue);
	}

	public List<UserModel> getListeningUsers() {
		return Collections.unmodifiableList(listeningUsers);
	}

	public UserModel getBroadcaster() {
		return broadcaster;
	}

	void removeBroadcasterPermanently(UserModel user) {
		if (broadcastQueue.contains(user)) {
			broadcastQueue.remove(user);
		} else {
			if (user.getId().equals(broadcaster.getId())) {
				synchronized (this) {
					broadcaster = broadcastQueue.poll();
					if (broadcaster == null) {
						broadcaster = listeningUsers.get(0);
						listeningUsers.remove(broadcaster);
					}
				}
			}
		}
	}

}
