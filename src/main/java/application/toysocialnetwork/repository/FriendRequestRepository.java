package application.toysocialnetwork.repository;

import application.toysocialnetwork.domain.FriendRequest;
import application.toysocialnetwork.domain.Tuple;
import application.toysocialnetwork.domain.dto.UserDTO;

import java.util.List;

public interface FriendRequestRepository extends Repository<Tuple<Long>, FriendRequest> {

    /**
     * Verifies if 2 users are friends
     * @param userId1 Long, first user id
     * @param userId2 Long, second user id
     * @return true, if the users are friends,
     *          false otherwise
     */
    boolean areFriends(Long userId1, Long userId2);

    List<UserDTO> findPendingRequestsOf(Long userId);
}
