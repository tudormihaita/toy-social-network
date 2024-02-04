package application.toysocialnetwork.repository.database;

import application.toysocialnetwork.domain.FriendRequest;
import application.toysocialnetwork.domain.RequestStatus;
import application.toysocialnetwork.domain.Tuple;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.FriendRequestRepository;
import application.toysocialnetwork.utils.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDBRepository extends AbstractDBRepository<Tuple<Long>, FriendRequest> implements FriendRequestRepository {

    public FriendRequestDBRepository(DataBase dataBase) {
        super(dataBase, "friend_requests");
    }

    @Override
    public FriendRequest extractEntity(ResultSet resultSet) throws SQLException {
        Long idFromUser = resultSet.getLong("id_from_user");
        Long idToUser = resultSet.getLong("id_to_user");
        LocalDateTime processDate = resultSet.getTimestamp("process_date").toLocalDateTime();
        RequestStatus requestStatus = RequestStatus.fromString(resultSet.getString("status"));

        FriendRequest friendRequest = new FriendRequest(idFromUser, idToUser, processDate, requestStatus);
        friendRequest.setId(new Tuple<>(idFromUser, idToUser));
        return friendRequest;
    }

    @Override
    protected PreparedStatement findStatement(Connection connection, Tuple<Long> id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM friend_requests " +
                "WHERE id_from_user = ? AND id_to_user = ?");
        preparedStatement.setLong(1, id.getFirst());
        preparedStatement.setLong(2, id.getSecond());

        return preparedStatement;
    }

    @Override
    protected PreparedStatement saveStatement(Connection connection, FriendRequest entity) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO friend_requests" +
                "(id_from_user, id_to_user, process_date, status) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT ON CONSTRAINT pk_friend_request DO UPDATE " +
                "SET process_date = excluded.process_date, " +
                "status = excluded.status");
        preparedStatement.setLong(1, entity.getIdFromUser());
        preparedStatement.setLong(2, entity.getIdToUser());
        preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getProcessDate()));
        preparedStatement.setString(4, entity.getStatus().getStatusAsString());

        return preparedStatement;
    }

    @Override
    protected PreparedStatement deleteStatement(Connection connection, Tuple<Long> id) throws SQLException {
        PreparedStatement preparedStatement =  connection.prepareStatement("DELETE FROM friend_requests " +
                "WHERE id_from_user = ? AND id_to_user = ?");
        preparedStatement.setLong(1, id.getFirst());
        preparedStatement.setLong(2, id.getSecond());

        return preparedStatement;
    }

    @Override
    protected PreparedStatement updateStatement(Connection connection, FriendRequest entity) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE friend_requests " +
                "SET process_date = ?, status = ? " +
                "WHERE id_from_user = ? AND id_to_user = ?");
        preparedStatement.setTimestamp(1, Timestamp.valueOf(entity.getProcessDate()));
        preparedStatement.setString(2, entity.getStatus().getStatusAsString());
        preparedStatement.setLong(3, entity.getIdFromUser());
        preparedStatement.setLong(4, entity.getIdToUser());

        return preparedStatement;
    }

    private boolean userExists(Long userId) throws RuntimeException {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT u.id_user FROM users u " +
                    "WHERE u.id_user = ?");
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public boolean areFriends(Long user1Id, Long user2Id) throws RuntimeException {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM friendships " +
                    "WHERE (id_user1 = ? AND id_user2 = ?) " +
                    "OR (id_user1 = ? AND id_user2 = ?)");
            preparedStatement.setLong(1, user1Id);
            preparedStatement.setLong(2, user2Id);
            preparedStatement.setLong(3, user2Id);
            preparedStatement.setLong(4, user1Id);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public List<UserDTO> findPendingRequestsOf(Long userId) throws RuntimeException {
        if(!userExists(userId)) {
            throw new InvalidIdentifierException("Invalid user identifier provided!");
        }

        List<UserDTO> pendingRequestsUsers = new ArrayList<>();
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT r.id_from_user AS id_from_user, " +
                    "u.first_name AS first_name, u.last_name AS last_name, u.birth_date AS birth_date, u.email AS email " +
                    "FROM friend_requests r " +
                    "INNER JOIN users u ON u.id_user = r.id_from_user " +
                    "WHERE status = ? AND id_to_user = ?");
            preparedStatement.setString(1, RequestStatus.PENDING.getStatusAsString());
            preparedStatement.setLong(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Long idToUser = resultSet.getLong("id_from_user");
                String firstNameUser = resultSet.getString("first_name");
                String lastNameUser = resultSet.getString("last_name");
                LocalDate birthDateUser = resultSet.getDate("birth_date").toLocalDate();
                String emailUser = resultSet.getString("email");

                UserDTO pendingUser = new UserDTO(idToUser, firstNameUser, lastNameUser, birthDateUser, emailUser);
                pendingRequestsUsers.add(pendingUser);
            }
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }

        return  pendingRequestsUsers;
    }
}
