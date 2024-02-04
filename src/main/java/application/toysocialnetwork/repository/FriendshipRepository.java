package application.toysocialnetwork.repository;

import application.toysocialnetwork.domain.dto.Friend;
import application.toysocialnetwork.domain.Friendship;
import application.toysocialnetwork.domain.Tuple;
import application.toysocialnetwork.utils.Page;
import application.toysocialnetwork.utils.Pageable;

import java.util.Set;

/**
 * Interface specific for the Friendship Repository, adding to the CRUD operations interface other
 * friendship-specific operations
 */
public interface FriendshipRepository extends Repository<Tuple<Long>, Friendship> {

    /**
     * Finds all friends of a given user and returns their ID's
     * @param userId Long, the provided user id
     * @return a Set with all the friends of a user
     */
    Set<Long> findFriendsGroup(Long userId);

    /**
     * Finds all friends of a User and returns a collection of Friends
     * @param userId Long, the provided user id
     * @return a Collection of Friends of the given user
     */
    Iterable<Friend> findFriendsOf(Long userId);

    /**
     * Finds a specified number of friends of a User, matching the current page size provided
     * @param pageable Pageable, contains the page size and the total element count of the current Pageable Repository
     * @param userId Long, the provided user id
     * @return a Page of Friends selected from the total friends list
     */
    Page<Friend> findFriendsOf(Pageable pageable, Long userId);
}
