package application.toysocialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import static application.toysocialnetwork.utils.Constants.DATE_TIME_FORMATTER;

/**
 * Class representing a friendship between 2 users once a request is accepted
 */
public class Friendship extends Entity<Tuple<Long>> {

    private final Long idUser1;
    private final Long idUser2;
    private final LocalDateTime friendsSince;

    //TODO: ask how should the constructor be changed now: maybe id set when creating object
    // and having DTOs as attributes inside the friendship; or IDs also as attributes; or
    // setting the id inside the constructor

    /**
     * Constructs a friendship
     * @param
     * friendsSince the date and time of when the friendship was accepted
     */
    public Friendship(Long idUser1, Long idUser2, LocalDateTime friendsSince) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.friendsSince = friendsSince;
    }

    public Friendship(Long idUser1, Long idUser2) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.friendsSince = LocalDateTime.now(); }

    /**
     * @return date when the friend request was accepted
     */
    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    /**
     * @return id of the first user
     */
    public Long getFirstUserId() {
        return this.getId().getFirst();
    }

    /**
     * @return id of the second user
     */
    public Long getSecondUserId() {
        return this.getId().getSecond();
    }

    public Long getIdUser1() {
        return idUser1;
    }

    public Long getIdUser2() {
        return idUser2;
    }

    @Override
    public String toString() {
        return "Friendship:" +
//                "( " + getFirstUserId() +
//                " ; " + getSecondUserId() + ") | " +
                "friends since: " + friendsSince.format(DATE_TIME_FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship)) return false;
        if (!super.equals(o)) return false;
        Friendship that = (Friendship) o;
        return (getFirstUserId().equals(that.getFirstUserId())  &&
                getSecondUserId().equals(that.getSecondUserId())) ||
                (getFirstUserId().equals(that.getSecondUserId()) &&
                        getSecondUserId().equals(that.getFirstUserId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), friendsSince);
    }
}