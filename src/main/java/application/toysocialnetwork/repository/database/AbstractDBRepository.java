package application.toysocialnetwork.repository.database;

import application.toysocialnetwork.domain.Entity;
import application.toysocialnetwork.exceptions.DBConnectionException;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.Repository;
import application.toysocialnetwork.utils.DataBase;
import application.toysocialnetwork.utils.Pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private final DataBase dataBase;
    private final String table;

    protected AbstractDBRepository(DataBase dataBase, String table) {
        this.dataBase = dataBase;
        this.table = table;
    }

    protected Connection getConnection() throws DBConnectionException {
        return dataBase.getConnection();
    }

    public abstract E extractEntity(ResultSet resultSet) throws SQLException;

    protected abstract PreparedStatement findStatement(Connection connection, ID id) throws SQLException;
    protected abstract PreparedStatement saveStatement(Connection connection, E entity) throws SQLException;
    protected abstract PreparedStatement deleteStatement(Connection connection, ID id) throws SQLException;
    protected abstract  PreparedStatement updateStatement(Connection connection, E entity) throws SQLException;

    @Override
    public Optional<E> findOne(ID id) throws InvalidIdentifierException {
        if(id == null) {
            throw new InvalidIdentifierException("The identifier must not be null!");
        }
        try(Connection connection = getConnection()) {
            ResultSet resultSet = findStatement(connection, id).executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(extractEntity(resultSet));
            }
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<E> save(E entity) throws InvalidIdentifierException {
        if(entity == null) {
            throw new InvalidIdentifierException("The entity must not be null");
        }

        try(Connection connection = getConnection()) {
            int affectedRows = saveStatement(connection, entity).executeUpdate();
            return affectedRows == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public Optional<E> delete(ID id) throws InvalidIdentifierException {
        if(id == null) {
            throw new InvalidIdentifierException("The entity must not be null!");
        }

        Optional<E> optionalEntity = findOne(id);

        if(optionalEntity.isPresent()) {
            try(Connection connection = getConnection()) {
                deleteStatement(connection, id).executeUpdate();
            }
            catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }

        return optionalEntity;
    }

    @Override
    public Optional<E> update(E entity) throws InvalidIdentifierException {
        if(entity == null) {
            throw new InvalidIdentifierException("The entity must not be null!");
        }
        Optional<E> optionalEntity = findOne(entity.getId());

        if(optionalEntity.isPresent()) {
            try (Connection connection = getConnection()) {
                updateStatement(connection, entity).executeUpdate();
                return Optional.empty();
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException.getMessage());
            }
        }

        return Optional.of(entity);
    }

    @Override
    public Iterable<E> findAll() {
        List<E> entities = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM " + table);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                E entity = extractEntity(resultSet);
                entities.add(entity);
            }

            return entities;
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException.getMessage());
        }
    }

}
