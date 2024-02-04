package application.toysocialnetwork.domain.dto;

import java.time.LocalDateTime;

public class Friend {
    private final Long friendId;
    private final String firstName;
    private final String lastName;
    private final LocalDateTime friendsSince;

    public Friend(Long friendId, String firstName, String lastName, LocalDateTime friendsSince) {
        this.friendId = friendId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendsSince = friendsSince;
    }

    public Long getFriendId() {
        return friendId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
