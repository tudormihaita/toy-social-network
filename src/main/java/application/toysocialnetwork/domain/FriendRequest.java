package application.toysocialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequest extends Entity<Tuple<Long>> {
    private final Long idFromUser;
    private final Long idToUser;
    private LocalDateTime processDate;
    private RequestStatus status;

    public FriendRequest(Long idFromUser, Long idToUser, LocalDateTime date, RequestStatus status) {
        this.idFromUser = idFromUser;
        this.idToUser = idToUser;
        this.processDate = date;
        this.status = status;
    }

    public FriendRequest(Long idFromUser, Long idToUser, RequestStatus status) {
        this.idFromUser = idFromUser;
        this.idToUser = idToUser;
        this.status = status;
        this.processDate = LocalDateTime.now();
    }

    public Long getIdFromUser() {
        return idFromUser;
    }

    public Long getIdToUser() {
        return idToUser;
    }

    public LocalDateTime getProcessDate() {
        return processDate;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setProcessDate(LocalDateTime processDate) {
        this.processDate = processDate;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendRequest that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getIdFromUser(), that.getIdFromUser()) && Objects.equals(getIdToUser(), that.getIdToUser()) && Objects.equals(getProcessDate(), that.getProcessDate()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdFromUser(), getIdToUser(), getProcessDate(), getStatus());
    }
}
