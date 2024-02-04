package application.toysocialnetwork.repository.database;

import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.UserRepository;
import application.toysocialnetwork.utils.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDBRepository  extends AbstractDBRepository<Long, User> implements UserRepository {

    public UserDBRepository(DataBase dataBase) {
        super(dataBase, "users");
    }

    @Override
    public User extractEntity(ResultSet resultSet)  throws SQLException {
        Long id = resultSet.getLong("id_user");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        LocalDate birthDate = resultSet.getDate("birth_date").toLocalDate();
        LocalDateTime registerDate = resultSet.getTimestamp("register_date").toLocalDateTime();

        User user = new User(firstName, lastName, email, password, birthDate, registerDate);
        user.setId(id);
        return user;
    }

    @Override
    protected PreparedStatement findStatement(Connection connection, Long id)  throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users " +
                " WHERE id_user = ?");
        preparedStatement.setLong(1, id);

        return preparedStatement;
    }

    @Override
    protected PreparedStatement saveStatement(Connection connection, User user) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users" +
                "(first_name, last_name, email, password, birth_date, register_date)" +
                " VALUES (?, ?, ?, ?, ?, ?)");

        preparedStatement.setString(1, user.getFirstName());
        preparedStatement.setString(2, user.getLastName());
        preparedStatement.setString(3, user.getEmailAddress());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setDate(5, Date.valueOf(user.getBirthDate()));
        preparedStatement.setTimestamp(6, Timestamp.valueOf(user.getRegisterDate()));

        return preparedStatement;
    }

    @Override
    protected PreparedStatement deleteStatement(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users " +
                "WHERE id_user = ?");
        preparedStatement.setLong(1, id);

        return preparedStatement;
    }

    @Override
    protected PreparedStatement updateStatement(Connection connection, User user) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users " +
                "SET first_name = ?, last_name = ?, email = ?, password = ?, birth_date = ? " +
                "WHERE id_user = ?");
        preparedStatement.setString(1, user.getFirstName());
        preparedStatement.setString(2, user.getLastName());
        preparedStatement.setString(3, user.getEmailAddress());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setDate(5, Date.valueOf(user.getBirthDate()));
        preparedStatement.setLong(6, user.getId());

        return preparedStatement;
    }

    @Override
    public Optional<User> findByEmail(String emailAddress) throws RuntimeException {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE " +
                    "email = ?");
            preparedStatement.setString(1, emailAddress);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return Optional.ofNullable(extractEntity(resultSet));
            }
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByLoginCredentials(String emailAddress, String password) throws RuntimeException {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users " +
                    "WHERE email = ? AND password = ?");
            preparedStatement.setString(1, emailAddress);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return Optional.ofNullable(extractEntity(resultSet));
            }
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<UserDTO> matchByName(String searchedName) throws RuntimeException {
        List<UserDTO> matchedUsers = new ArrayList<>();
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id_user, first_name, " +
                    "last_name, birth_date, email FROM users " +
                    "WHERE CONCAT(first_name, ' ', last_name) LIKE ?");

            searchedName = "%" + searchedName + "%";
            preparedStatement.setString(1, searchedName);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Long userId = resultSet.getLong("id_user");
                String userFirstName = resultSet.getString("first_name");
                String userLastName = resultSet.getString("last_name");
                LocalDate userBirthDate = resultSet.getDate("birth_date").toLocalDate();
                String userEmail = resultSet.getString("email");
                UserDTO userDTO = new UserDTO(userId, userFirstName, userLastName, userBirthDate, userEmail);
                matchedUsers.add(userDTO);
            }
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }

        return matchedUsers;
    }
}
