package application.toysocialnetwork.repository.database;

import application.toysocialnetwork.domain.message.Message;
import application.toysocialnetwork.domain.message.ReplyMessage;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.MessageRepository;
import application.toysocialnetwork.utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDBRepository extends AbstractDBRepository<Long, Message> implements MessageRepository {

    public MessageDBRepository(DataBase dataBase) {
        super(dataBase, "messages");
    }

    @Override
    public Message extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id_message");
        Long idSender = resultSet.getLong("id_sender");
        String text = resultSet.getString("text");
        LocalDateTime sentAt = resultSet.getTimestamp("sent_at").toLocalDateTime();
        Long replyTo = resultSet.getLong("reply_to");

        Message message = new Message(idSender, text, sentAt, replyTo);
        message.setId(id);
        return message;
    }

    @Override
    protected PreparedStatement findStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM messages " +
                "WHERE id_message = ?");
        preparedStatement.setLong(1, id);

        return preparedStatement;
    }

    @Override
    protected PreparedStatement saveStatement(Connection connection, Message entity) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO messages" +
                "(id_sender, text, sent_at, reply_to) " +
                "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setLong(1, entity.getIdSender());
        preparedStatement.setString(2, entity.getText());
        preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getSentAt()));
        if(entity.getReplyTo() != null) {
            preparedStatement.setLong(4, entity.getReplyTo());
        }
        else {
            preparedStatement.setNull(4, Types.BIGINT);
        }

        return preparedStatement;
    }

    @Override
    protected PreparedStatement deleteStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM messages " +
                "WHERE id_message = ?");
        preparedStatement.setLong(1, id);

        return preparedStatement;
    }

    @Override
    protected PreparedStatement updateStatement(Connection connection, Message entity) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE messages " +
                "SET text = ?, sent_at = ?, reply_to = ? " +
                "WHERE id_message = ?");
        preparedStatement.setString(1, entity.getText());
        preparedStatement.setTimestamp(2, Timestamp.valueOf(entity.getSentAt()));
        preparedStatement.setLong(3, entity.getReplyTo());

        return preparedStatement;
    }

    @Override
    public int saveReceivers(Message sentMessage, List<Long> receivers) throws RuntimeException {
        try(Connection connection = getConnection()) {
            StringBuilder sql = new StringBuilder("INSERT INTO sent_to(id_message, id_receiver) VALUES");
            for(int i = 0; i < receivers.size(); i++) {
                String entry = " (?, ?)";
                sql.append(entry);
                if(i+1 != receivers.size()) {
                    sql.append(", ");
                }
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            int index = 1;
            for(Long receiver: receivers) {
                preparedStatement.setLong(index++, sentMessage.getId());
                preparedStatement.setLong(index++, receiver);
            }

            return preparedStatement.executeUpdate();
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public List<Message> findMessagesBetween(Long idSender, Long idReceiver) throws RuntimeException {
        List<Message> messages = new ArrayList<>();
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT m.id_message, " +
                    "m.id_sender, m.text, m.sent_at, m.reply_to, " +
                    "r.text AS replied_text FROM messages m " +
                    "LEFT JOIN messages r ON m.reply_to = r.id_message " +
                    "INNER JOIN sent_to st on m.id_message = st.id_message " +
                    "WHERE (m.id_sender = ? AND st.id_receiver = ?) " +
                    "OR (m.id_sender = ? AND st.id_receiver = ?) " +
                    "ORDER BY m.sent_at");
           preparedStatement.setLong(1, idSender);
           preparedStatement.setLong(2, idReceiver);
           preparedStatement.setLong(3, idReceiver);
           preparedStatement.setLong(4, idSender);

           ResultSet resultSet = preparedStatement.executeQuery();
           while(resultSet.next()) {
               Long id = resultSet.getLong("id_message");
               Long sender = resultSet.getLong("id_sender");
               String text = resultSet.getString("text");
               LocalDateTime sentAt = resultSet.getTimestamp("sent_at").toLocalDateTime();
               Long replyTo = resultSet.getLong("reply_to");
               String repliedText = resultSet.getString("replied_text");

               if(repliedText == null) {
                   Message message = new Message(sender, text, sentAt, replyTo);
                   message.setId(id);
                   messages.add(message);
               }
               else {
                   ReplyMessage replyMessage = new ReplyMessage(sender, text, sentAt, replyTo, repliedText);
                   replyMessage.setId(id);
                   messages.add(replyMessage);
               }
           }
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }

        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) throws InvalidIdentifierException {
        if(entity == null) {
            throw new InvalidIdentifierException("The entity must not be null");
        }

        try(Connection connection = getConnection()) {
            PreparedStatement saveStatement = saveStatement(connection, entity);
            int affectedRows = saveStatement.executeUpdate();
            if(affectedRows != 0) {
                ResultSet generatedID = saveStatement.getGeneratedKeys();
                if(generatedID.next()) {
                    entity.setId(generatedID.getLong(1));
                }
                return Optional.empty();
            }
            else {
                return Optional.of(entity);
            }

        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }
    }
}
