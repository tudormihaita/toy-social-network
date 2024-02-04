package application.toysocialnetwork.utils.events;

import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.message.Message;

public class MessageSendEvent implements ProfileUpdateEvent {
    private SendEventType type;
    private Message newData, oldData;

    public MessageSendEvent(SendEventType type, Message newData) {
        this.type = type;
        this.newData = newData;
    }

    public MessageSendEvent(SendEventType type, Message oldData, Message newData) {
        this.type = type;
        this.newData = newData;
        this.oldData =  oldData;
    }

    public SendEventType getType() {
        return type;
    }

    public Message getNewData() {
        return newData;
    }

    public Message getOldData() {
        return oldData;
    }
}
