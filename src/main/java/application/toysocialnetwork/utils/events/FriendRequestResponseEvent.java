package application.toysocialnetwork.utils.events;

import application.toysocialnetwork.domain.FriendRequest;
import application.toysocialnetwork.domain.RequestStatus;

//TODO: ask for Observer implementation in ProfileController, which listens to multiple types of events at once

public class FriendRequestResponseEvent implements ProfileUpdateEvent {
    private RequestStatus requestStatus;
    private FriendRequest newData, oldData;

    public FriendRequestResponseEvent(RequestStatus status, FriendRequest newData) {
        this.requestStatus = status;
        this.newData = newData;
    }

    public FriendRequestResponseEvent(RequestStatus status, FriendRequest oldData, FriendRequest newData) {
        this.requestStatus = status;
        this.oldData = oldData;
        this.newData = newData;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public FriendRequest getNewData() {
        return newData;
    }

    public FriendRequest getOldData() {
        return oldData;
    }
}
