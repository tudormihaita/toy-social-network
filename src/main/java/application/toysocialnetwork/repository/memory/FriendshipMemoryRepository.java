package application.toysocialnetwork.repository.memory;

import application.toysocialnetwork.domain.dto.Friend;
import application.toysocialnetwork.domain.Friendship;
import application.toysocialnetwork.domain.Tuple;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.FriendshipRepository;
import application.toysocialnetwork.repository.UserRepository;
import application.toysocialnetwork.utils.Page;
import application.toysocialnetwork.utils.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Repository class for the Friendship entity, which extends the Abstract Memory Repository by
 *  implementing the FriendshipRepository interface, with friendship-specific operations also added
 */
public class FriendshipMemoryRepository extends AbstractMemoryRepository<Tuple<Long>, Friendship> implements FriendshipRepository {

    UserRepository userRepository;

    /**
     * Constructs a new friendship repo and initializes attributes
     * @param userRepository UserRepository, reference to the user repo
     */
    public FriendshipMemoryRepository(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    /**
     * Checks if a user exists in the user repository
     * @param userId Long, user identifier
     * @return true if it exists,
     *          false otherwise
     */
    private boolean userExists(Long userId) {
        return userRepository.findOne(userId).isPresent();
    }

    /**
     * Overrides the add method of the CRUD repository, by additionally checking
     * if the provided users exist
     * @param friendship E, new entity
     * @return E, the entity if it already existed,
     *          null otherwise
     * @throws InvalidIdentifierException if the provided entity is null or the users don't exist
     */
    @Override
    public Optional<Friendship> save(Friendship friendship) throws InvalidIdentifierException {
        if(userExists(friendship.getFirstUserId()) && userExists(friendship.getSecondUserId())) {
            return super.save(friendship);
        }
        else {
            throw new InvalidIdentifierException("Invalid entity identifiers provided!");
        }
    }

    /**
     * Finds all friends of a given user
     * @param userId Long, the provided user id
     * @return a Set with all the friends of a user
     */
    @Override
    public Set<Long> findFriendsGroup(Long userId) throws InvalidIdentifierException {
        Set<Long> users = new HashSet<>();

        entities.forEach((k,v) -> {
            if(v.getFirstUserId().equals(userId)) {
                users.add(v.getSecondUserId());
            }
            else if(v.getSecondUserId().equals(userId)) {
                users.add(v.getFirstUserId());
            }
        });

        return users;
    }

    @Override
    public Iterable<Friend> findFriendsOf(Long userId) {
        List<Friend> friends = new ArrayList<>();

        entities.forEach((k, v) -> {
            Long friendId = null;
            if (v.getFirstUserId().equals(userId)) {
                friendId = v.getSecondUserId();
            } else if (v.getSecondUserId().equals(userId)) {
                friendId = v.getFirstUserId();
            }
            if (friendId != null) {
                Optional<User> userFriend = userRepository.findOne(friendId);
                if (userFriend.isPresent()) {
                    String firstName = userFriend.get().getFirstName();
                    String lastName = userFriend.get().getLastName();
                    LocalDateTime friendsSince = v.getFriendsSince();
                    Friend friend = new Friend(friendId, firstName, lastName, friendsSince);
                    friends.add(friend);
                }
            }

        });

        return friends;
    }

    @Override
    public Page<Friend> findFriendsOf(Pageable pageable, Long userId) {
        return null;
    }
}