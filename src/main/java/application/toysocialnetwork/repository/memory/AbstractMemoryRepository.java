package application.toysocialnetwork.repository.memory;

import application.toysocialnetwork.domain.Entity;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class implementing the generic CRUD operations repository, with data being memory-persistent
 * @param <ID> ID, the data type for the identifiers of the entities stored
 * @param <E> E, the data type for the entities stored
 */
public abstract class AbstractMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    protected Map<ID, E> entities;

    /**
     * Constructs a new Memory Repository, with an Empty HashMap
     */
    protected AbstractMemoryRepository() {
        entities = new HashMap<>();
    }

    /**
     * Finds an entity stored searched by id
     * @param id ID, identifier of the entity stored
     * @return E, the entity if it is found,
     *          null otherwise
     * @throws InvalidIdentifierException if the provided identifier is null
     */
    @Override
    public Optional<E> findOne(ID id) throws InvalidIdentifierException {
        if(id == null) {
            throw new InvalidIdentifierException("The entity's identifier must not be null!");
        }

        return Optional.ofNullable(entities.get(id));

    }

    /**
     * Finds all entities stored and returns them in a List
     * @return List<E>, list of all entities
     */
    @Override
    public List<E> findAll() {
        return entities.values().stream().toList();
    }

    /**
     * Adds a new entity to the memory repository
     * @param entity E, new entity
     * @return E, the entity if it already exists,
     *      null otherwise
     * @throws InvalidIdentifierException if the provided entity is null
     */
    @Override
    public Optional<E> save(E entity) throws InvalidIdentifierException {
        if (entity == null) {
            throw new InvalidIdentifierException("The entity to be added cannot be null!");
        }

        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    /**
     * Deletes an entity from the memory repository
     * @param id ID, identifier of the desired entity
     * @return E, the removed entity if it is found,
     *          null otherwise
     * @throws InvalidIdentifierException if the provided identifier is null
     */
    @Override
    public Optional<E> delete(ID id) throws InvalidIdentifierException {
        if(id == null) {
            throw new InvalidIdentifierException("The to-be-deleted entity's identifier cannot be null!");
        }

        return Optional.ofNullable(entities.remove(id));
    }

    /**
     * Updates an already existing entity from the memory repo with an updated one
     * @param entity E, updated entity to replace the currently existing one
     * @return E, the entity if it was not found and not updated,
     *          null otherwise
     * @throws InvalidIdentifierException if the provided updated entity is null
     */
    @Override
    public Optional<E> update(E entity) throws InvalidIdentifierException{
        if(entity == null) {
            throw new InvalidIdentifierException("Entity to be updated cannot be null!");
        }

        Optional<E> optionalEntity = findOne(entity.getId());
        if(optionalEntity.isEmpty()) {
            return Optional.of(entity);
        }

        entities.replace(entity.getId(), entity);
        return Optional.empty();
    }
}