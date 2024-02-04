package application.toysocialnetwork.utils.events;

import application.toysocialnetwork.domain.User;

public class UserChangeEvent implements ProfileUpdateEvent {
    private ChangeEventType type;
    private User newData, oldData;

    public UserChangeEvent(ChangeEventType type, User newData) {
        this.type = type;
        this.newData = newData;
    }

    public UserChangeEvent(ChangeEventType type, User oldData, User newData) {
        this.type = type;
        this.newData = newData;
        this.oldData =  oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getNewData() {
        return newData;
    }

    public User getOldData() {
        return oldData;
    }
}
