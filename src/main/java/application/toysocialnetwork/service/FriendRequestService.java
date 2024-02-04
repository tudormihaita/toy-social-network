package application.toysocialnetwork.service;

import application.toysocialnetwork.domain.FriendRequest;
import application.toysocialnetwork.domain.RequestStatus;
import application.toysocialnetwork.domain.Tuple;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.exceptions.NotFoundException;
import application.toysocialnetwork.repository.FriendRequestRepository;
import application.toysocialnetwork.utils.events.FriendRequestResponseEvent;
import application.toysocialnetwork.utils.events.ProfileUpdateEvent;
import application.toysocialnetwork.utils.observer.Observable;
import application.toysocialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestService implements Observable<ProfileUpdateEvent> {
    private final FriendRequestRepository repository;

    private List<Observer<ProfileUpdateEvent>> observers = new ArrayList<>();

    public FriendRequestService(FriendRequestRepository friendRequestRepository) {
        this.repository = friendRequestRepository;
    }

    public void sendFriendRequest(Long idFromUser, Long idToUser) throws InvalidIdentifierException, NotFoundException {
        if(repository.areFriends(idFromUser, idToUser)) {
            throw new InvalidIdentifierException("Selected users are already friends!");
        }
        if(idFromUser.equals(idToUser)) {
            throw new InvalidIdentifierException("Invalid identifiers provided, must be distinct!");
        }

        FriendRequest friendRequest = new FriendRequest(idFromUser, idToUser, RequestStatus.PENDING);
        if(repository.save(friendRequest).isPresent()) {
            throw new NotFoundException("Given identifier not found!");
        }

        notify(new FriendRequestResponseEvent(RequestStatus.PENDING, null, friendRequest));
    }

    public void acceptFriendRequest(Long idFromUser, Long idToUser) throws InvalidIdentifierException {
       FriendRequest acceptedRequest = new FriendRequest(idFromUser, idToUser, RequestStatus.APPROVED);
       acceptedRequest.setId(new Tuple<>(idFromUser, idToUser));

       if(repository.update(acceptedRequest).isPresent()) {
           throw new InvalidIdentifierException("Entity with given identifier doesn't exist!");
       }

       notify(new FriendRequestResponseEvent(RequestStatus.APPROVED, null, acceptedRequest));
    }

    public void declineFriendRequest(Long idFromUser, Long idToUser) throws InvalidIdentifierException {
        FriendRequest declinedRequest = new FriendRequest(idFromUser, idToUser, RequestStatus.REJECTED);
        declinedRequest.setId(new Tuple<>(idFromUser, idToUser));

        if(repository.update(declinedRequest).isPresent()) {
            throw new InvalidIdentifierException("Entity with given identifier doesn't exist!");
        }

        notify(new FriendRequestResponseEvent(RequestStatus.REJECTED, declinedRequest));
    }

    public void cancelFriendRequest(Long idFromUser, Long idToUser) throws InvalidIdentifierException {
        Optional<FriendRequest> cancelledRequest = repository.delete(new Tuple<>(idFromUser, idToUser));
        if(cancelledRequest.isEmpty()) {
            throw new InvalidIdentifierException("Entity with given identifier doesn't exist!");
        }

        notify(new FriendRequestResponseEvent(RequestStatus.CANCELLED, cancelledRequest.get(), null));
    }

    public List<UserDTO> findPendingRequestsOf(Long idToUser) throws RuntimeException {
        return repository.findPendingRequestsOf(idToUser);
    }

    public boolean areFriends(Long idFromUser, Long idToUser) throws RuntimeException {
        return repository.areFriends(idFromUser, idToUser);
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
