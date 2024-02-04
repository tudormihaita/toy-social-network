package application.toysocialnetwork.repository;

import application.toysocialnetwork.domain.Entity;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.exceptions.ValidationException;

import java.util.Optional;

/**
 * Interface for a CRUD operations repository
 * @param <ID> Identifier of the entities stored in the repository
 * @param <E> Data type for the entities stored
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Searches for an entity by identifier and returns it
     *
     * @param id ID, the identifier of the entity to be returned
     *           id must not be null
     * @return an {@code Optional} encapsulating the entity with the given id
     */
    Optional<E> findOne(ID id) throws InvalidIdentifierException;

    /**
     * Finds all entities stored and returns them in an Iterable Data Structure
     *
     * @return Iterable<E>, a collection of all stored entities
     */
    Iterable<E> findAll();

    /**
     * Adds a new entity to the repository
     *
     * @param entity E, new entity added
     * @return an {@code Optional} - null if the entity was saved,
     * the entity if id already exists
     * @throws ValidationException        if the given entity fails to be validated
     * @throws InvalidIdentifierException if the given entity is null
     */
    Optional<E> save(E entity) throws InvalidIdentifierException;

    /**
     * Deletes an entity with the specified id
     *
     * @param id - ID, the identifier of the desired entity
     * @return an {@code Optional} - null if there is no entity with the given id.
     * the removed entity otherwise
     * @throws InvalidIdentifierException if the given id is null or not existent
     */
    Optional<E> delete(ID id) throws InvalidIdentifierException;

    /**
     * Updates an already existing entity
     *
     * @param entity E, the updated entity to replace the currently existing one
     * @return an {@code Optional} - null if the entity was updated,
     * the entity otherwise (e.g. id does not exist)
     * @throws ValidationException        if the updated entity fails to be validated
     * @throws InvalidIdentifierException if given entity is null
     */
    Optional<E> update(E entity) throws InvalidIdentifierException;
}
