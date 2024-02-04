package application.toysocialnetwork.service;

import application.toysocialnetwork.domain.message.Message;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.MessageRepository;
import application.toysocialnetwork.utils.events.MessageSendEvent;
import application.toysocialnetwork.utils.events.ProfileUpdateEvent;
import application.toysocialnetwork.utils.events.SendEventType;
import application.toysocialnetwork.utils.observer.Observable;
import application.toysocialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService implements Observable<ProfileUpdateEvent> {
    private final MessageRepository repository;

    private List<Observer<ProfileUpdateEvent>> observers = new ArrayList<>();

    public MessageService(MessageRepository messageRepository) {
        this.repository = messageRepository;
    }

    public void sendMessage(Long idSender, String text, List<Long> receivers) throws RuntimeException {
        Message message = new Message(idSender, text);

        if(repository.save(message).isPresent()) {
            throw new InvalidIdentifierException("Given message sender identifier is invalid!");
        }

        if(repository.saveReceivers(message, receivers) != receivers.size()) {
            throw new InvalidIdentifierException("Given receivers identifiers are invalid!");
        }

        notify(new MessageSendEvent(SendEventType.SEND, null, message));
    }

    public void sendReply(Long idSender, String text, List<Long> receivers, Long replyTo) throws RuntimeException {
        Message reply = new Message(idSender, text);
        reply.setReplyTo(replyTo);

        if(repository.save(reply).isPresent()) {
            throw new InvalidIdentifierException("Given message sender identifier is invalid!");
        }
        if(repository.saveReceivers(reply, receivers) != receivers.size()) {
            throw new InvalidIdentifierException("Given receivers identifiers are invalid!");
        }

        notify(new MessageSendEvent(SendEventType.SEND, null, reply));
    }

    public void unsendMessage(Long idMessage) throws InvalidIdentifierException {
        Optional<Message> unsentMessage = repository.delete(idMessage);
        if(unsentMessage.isEmpty()) {
            throw new InvalidIdentifierException("Entity with given identifier doesn't exist!");
        }

        notify(new MessageSendEvent(SendEventType.UNSEND, unsentMessage.get(), null));
    }

    public List<Message> findMessagesBetween(Long idUser, Long idContact) {
        return repository.findMessagesBetween(idUser, idContact);
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
