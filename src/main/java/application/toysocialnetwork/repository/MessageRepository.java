package application.toysocialnetwork.repository;

import application.toysocialnetwork.domain.message.Message;

import java.util.List;

public interface MessageRepository extends Repository<Long, Message> {
    int saveReceivers(Message sentMessage, List<Long> receivers);

    List<Message> findMessagesBetween(Long idSender, Long idReceiver);
}
