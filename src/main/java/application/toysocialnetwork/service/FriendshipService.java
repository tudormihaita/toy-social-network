package application.toysocialnetwork.service;

import application.toysocialnetwork.domain.dto.Friend;
import application.toysocialnetwork.domain.Friendship;
import application.toysocialnetwork.domain.Tuple;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.exceptions.ValidationException;
import application.toysocialnetwork.repository.FriendshipRepository;
import application.toysocialnetwork.utils.Page;
import application.toysocialnetwork.utils.Pageable;
import application.toysocialnetwork.utils.events.ChangeEventType;
import application.toysocialnetwork.utils.events.FriendRequestResponseEvent;
import application.toysocialnetwork.utils.events.FriendshipChangeEvent;
import application.toysocialnetwork.utils.events.ProfileUpdateEvent;
import application.toysocialnetwork.utils.observer.Observable;
import application.toysocialnetwork.utils.observer.Observer;
import application.toysocialnetwork.validators.ValidationStrategy;
import application.toysocialnetwork.validators.Validator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Class for the Friendship entity, responsible for all business logic
 */
public class FriendshipService implements Observable<ProfileUpdateEvent> {

    private final FriendshipRepository repository;

    private final Validator<Friendship> validator;

    private List<Observer<ProfileUpdateEvent>> observers = new ArrayList<>();

    /**
     * Initializes the Service
     * @param repository FriendshipRepository, reference to the repo
     * @param validator Validator for Friendship entity
     */
    public FriendshipService(FriendshipRepository repository, Validator<Friendship> validator) {
        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Finds all existing friendships and returns a List
     * @return List<Friendship>
     */
    public List<Friendship> findAll() {
        List<Friendship> friendshipList = new ArrayList<>();
        repository.findAll().forEach(friendshipList::add);

        return friendshipList;
    }

    /**
     * Adds a new friend for a user
     * @param userId Long, user identifier
     * @param newFriendId Long, new friend identifier
     * @throws InvalidIdentifierException if the friendship already exists or any provided identifiers are not valid
     * @throws ValidationException if friendship fails to be validated
     */
    public void addFriend(Long userId, Long newFriendId) throws InvalidIdentifierException, ValidationException {
        if(userId == null || newFriendId == null) {
            throw new InvalidIdentifierException("At least one provided identifier is not valid!");
        }

        Friendship friendship = new Friendship(userId, newFriendId);
        friendship.setId(new Tuple<>(userId, newFriendId));
        validator.validate(friendship, ValidationStrategy.QUICK);
        if(repository.save(friendship).isPresent()) {
            throw new InvalidIdentifierException("Given identifier already taken!");
        }

        notify(new FriendshipChangeEvent(ChangeEventType.ADD, null, friendship));
    }

    /**
     * Removes a friend from a user
     * @param userId Long, user identifier
     * @param friendId Long, friend identifier
     * @throws InvalidIdentifierException if any of the provided identifiers are not valid
     */
    public void removeFriend(Long userId, Long friendId) throws InvalidIdentifierException {
        if(userId == null || friendId == null) {
            throw new InvalidIdentifierException("At least one provided identifier is not valid!");
        }

        Optional<Friendship> deletedFriendship = repository.delete(new Tuple<>(userId, friendId));
        if(deletedFriendship.isEmpty()) {
            throw new InvalidIdentifierException("Entity with given identifier doesn't exist!");
        }

        notify(new FriendshipChangeEvent(ChangeEventType.DELETE, deletedFriendship.get()));
    }

    // public void updateFriendship(Long user1Id, Long user2Id) {}

    /**
     * Finds all friends of a user
     * @param userId Long, user identifier
     * @return a Set of all friends of the user
     * @throws InvalidIdentifierException if provided identifier doesn't exist
     */
    public Set<Long> findFriendsGroup(Long userId) throws InvalidIdentifierException {
        if(userId == null) {
            throw new InvalidIdentifierException("Invalid entity identifier provided!");
        }

        return repository.findFriendsGroup(userId);
    }

    public Map<Long, LocalDateTime> findFriendshipsSince(Long userId, Integer month) {
        Iterable<Friendship> friendships = repository.findAll();
        List<Friendship> friendshipList = new ArrayList<>();
        friendships.forEach(friendshipList::add);

        return friendshipList.stream()
                .filter(friendship ->
                        (friendship.getFirstUserId().equals(userId) ||
                                friendship.getSecondUserId().equals(userId)) &&
                                friendship.getFriendsSince().getMonth().getValue() == month)
                .collect(Collectors.toMap(
                        friendship ->  friendship.getFirstUserId().equals(userId) ?
                                friendship.getSecondUserId() : friendship.getFirstUserId(),
                        Friendship::getFriendsSince));

    }

    public List<Friend> findFriendsOf(Long userId) throws InvalidIdentifierException {
        if(userId == null) {
            throw new InvalidIdentifierException("Invalid entity identifier provided!");
        }

        return StreamSupport.stream(repository.findFriendsOf(userId).spliterator(),
                false).toList();
    }

    public Page<Friend> findFriendsOf(Pageable pageable, Long userId) throws RuntimeException {
        return repository.findFriendsOf(pageable, userId);
    }


    @Override
    public void addObserver(Observer<ProfileUpdateEvent> o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer<ProfileUpdateEvent> o) {
        observers.remove(o);
    }

    @Override
    public void notify(ProfileUpdateEvent t) {
        observers.forEach(o -> o.update(t));
    }
}
