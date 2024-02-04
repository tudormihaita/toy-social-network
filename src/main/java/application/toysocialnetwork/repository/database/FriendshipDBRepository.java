package application.toysocialnetwork.repository.database;

import application.toysocialnetwork.domain.dto.Friend;
import application.toysocialnetwork.domain.Friendship;
import application.toysocialnetwork.domain.Tuple;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.FriendshipRepository;
import application.toysocialnetwork.utils.DataBase;
import application.toysocialnetwork.utils.Page;
import application.toysocialnetwork.utils.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipDBRepository extends AbstractDBRepository<Tuple<Long>, Friendship> implements FriendshipRepository {

    public FriendshipDBRepository(DataBase database) {
        super(database, "friendships");
    }

    @Override
    public Friendship extractEntity(ResultSet resultSet) throws SQLException {
        Long user1Id = resultSet.getLong("id_user1");
        Long user2Id = resultSet.getLong("id_user2");
        LocalDateTime friendsSince = resultSet.getTimestamp("friends_since").toLocalDateTime();

        Friendship friendship = new Friendship(user1Id, user2Id, friendsSince);
        friendship.setId(new Tuple<>(user1Id, user2Id));
        return friendship;
    }

    @Override
    protected PreparedStatement findStatement(Connection connection, Tuple<Long> id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM friendships " +
                "WHERE (id_user1 = ? AND id_user2 = ?) " +
                "OR (id_user1 = ? AND id_user2 = ?)");
        preparedStatement.setLong(1, id.getFirst());
        preparedStatement.setLong(2, id.getSecond());
        preparedStatement.setLong(3, id.getSecond());
        preparedStatement.setLong(4, id.getFirst());

        return preparedStatement;
    }

    @Override
    protected PreparedStatement saveStatement(Connection connection, Friendship friendship) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO friendships" +
                "(id_user1, id_user2, friends_since) VALUES (?, ?, ?)");
        preparedStatement.setLong(1, friendship.getFirstUserId());
        preparedStatement.setLong(2, friendship.getSecondUserId());
        preparedStatement.setTimestamp(3, Timestamp.valueOf(friendship.getFriendsSince()));

        return preparedStatement;
    }

    @Override
    protected PreparedStatement deleteStatement(Connection connection, Tuple<Long> id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM friendships " +
                "WHERE (id_user1 = ? AND id_user2 = ?) " +
                "OR (id_user1 = ? AND id_user2 = ?)");
        preparedStatement.setLong(1, id.getFirst());
        preparedStatement.setLong(2, id.getSecond());
        preparedStatement.setLong(3, id.getSecond());
        preparedStatement.setLong(4, id.getFirst());

        return preparedStatement;
    }

    @Override
    protected  PreparedStatement updateStatement(Connection connection, Friendship friendship) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE friendships " +
                "SET friends_since = ? " +
                "WHERE (id_user1 = ? AND id_user2 = ?) " +
                "OR (id_user1 = ? AND id_user2 = ?)");

        preparedStatement.setTimestamp(1, Timestamp.valueOf(friendship.getFriendsSince()));
        preparedStatement.setLong(1, friendship.getFirstUserId());
        preparedStatement.setLong(2, friendship.getSecondUserId());
        preparedStatement.setLong(3, friendship.getSecondUserId());
        preparedStatement.setLong(4, friendship.getFirstUserId());

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
    public Set<Long> findFriendsGroup(Long userId) throws RuntimeException {
        if(!userExists(userId)) {
            throw new InvalidIdentifierException("Invalid user identifier provided!");
        }
        Set<Long> friends = new HashSet<>();
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT f1.id_user1 AS id_user FROM friendships f1 " +
                    "WHERE f1.id_user2 = ? " +
                    "UNION " +
                    "SELECT f2.id_user2 AS id_user FROM friendships f2 " +
                    "WHERE f2.id_user1 = ?");

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Long id = resultSet.getLong("id_user");
                friends.add(id);
            }

            return friends;
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public Iterable<Friend> findFriendsOf(Long userId) throws RuntimeException {
        if(!userExists(userId)) {
            throw new InvalidIdentifierException("Invalid user identifier provided!");
        }

        List<Friend> friends = new ArrayList<>();
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT f.id_user1 AS id_user, " +
                    "u.first_name, u.last_name, f.friends_since " +
                    "FROM friendships f " +
                    "INNER JOIN users u ON u.id_user = f.id_user1 " +
                    "WHERE f.id_user2 = ? " +
                    "UNION " +
                    "SELECT f.id_user2 AS id_user, u.first_name, u.last_name, f.friends_since " +
                    "FROM friendships f " +
                    "INNER JOIN users u ON u.id_user = f.id_user2 " +
                    "WHERE f.id_user1 = ?"
            );

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Long id = resultSet.getLong("id_user");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                LocalDateTime friendsSince = resultSet.getTimestamp("friends_since").toLocalDateTime();

                Friend friend = new Friend(id, firstName, lastName, friendsSince);
                friends.add(friend);
            }
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }

        return friends;
    }


    @Override
    public Page<Friend> findFriendsOf(Pageable pageable, Long userId) throws RuntimeException {
        if(!userExists(userId)) {
            throw new InvalidIdentifierException("Invalid user identifier provided!");
        }

        List<Friend> friendsList = new ArrayList<>();
        String selectFriendsSql = "SELECT f.id_user1 AS id_user, " +
                "u.first_name, u.last_name, f.friends_since " +
                "FROM friendships f " +
                "INNER JOIN users u ON u.id_user = f.id_user1 " +
                "WHERE f.id_user2 = ?" +
                " UNION " +
                "SELECT f.id_user2 AS id_user, " +
                "u.first_name, u.last_name, f.friends_since " +
                "FROM friendships f " +
                "INNER JOIN users u ON u.id_user = f.id_user2 " +
                "WHERE f.id_user1 = ?";

        String limitSql = " LIMIT ? OFFSET ?";
        String countTotalSql;
        String countSql = "SELECT COUNT(*) AS count";
        String countFriendsUser1Sql = " FROM friendships f " +
                "INNER JOIN users u ON u.id_user = f.id_user1 " +
                "WHERE f.id_user2 = ?";
        String countFriendsUser2Sql = " FROM friendships f " +
                "INNER JOIN users u ON u.id_user = f.id_user2 " +
                "WHERE f.id_user1 = ?";

        selectFriendsSql += limitSql;
        countTotalSql = "SELECT ( " + countSql + countFriendsUser1Sql + ") " + "+" + " ( " + countSql + countFriendsUser2Sql + ") " + " AS total_count";

        try(Connection connection = getConnection();
            PreparedStatement pagePreparedStatement = connection.prepareStatement(selectFriendsSql);
            PreparedStatement countPreparedStatement = connection.prepareStatement(countTotalSql);

            ) {

            pagePreparedStatement.setLong(1, userId);
            pagePreparedStatement.setLong(2, userId);
            pagePreparedStatement.setInt(3, pageable.getPageSize());
            pagePreparedStatement.setInt(4, pageable.getPageSize() * pageable.getPageNumber());

            countPreparedStatement.setLong(1, userId);
            countPreparedStatement.setLong(2, userId);

            ResultSet pageResultSet = pagePreparedStatement.executeQuery();
            ResultSet countResultSet = countPreparedStatement.executeQuery();
            while(pageResultSet.next()) {
                Long id = pageResultSet.getLong("id_user");
                String firstName = pageResultSet.getString("first_name");
                String lastName = pageResultSet.getString("last_name");
                LocalDateTime friendsSince = pageResultSet.getTimestamp("friends_since").toLocalDateTime();

                Friend friend = new Friend(id, firstName, lastName, friendsSince);
                friendsList.add(friend);
            }

            int totalCount = 0;
            if(countResultSet.next()) {
                totalCount = countResultSet.getInt("total_count");
            }

            return new Page<>(friendsList, totalCount);
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }
    }
}
