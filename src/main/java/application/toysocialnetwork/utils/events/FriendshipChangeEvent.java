package application.toysocialnetwork.utils.events;

import application.toysocialnetwork.domain.Friendship;


public class FriendshipChangeEvent implements ProfileUpdateEvent {
    private ChangeEventType type;
    private Friendship newData, oldData;

    public FriendshipChangeEvent(ChangeEventType type, Friendship newData) {
        this.type = type;
        this.newData = newData;
    }

    public FriendshipChangeEvent(ChangeEventType type, Friendship oldData, Friendship newData) {
        this.type = type;
        this.newData = newData;
        this.oldData =  oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Friendship getNewData() {
        return newData;
    }

    public Friendship getOldData() {
        return oldData;
    }
}
