package application.toysocialnetwork.domain;


import java.io.Serializable;
import java.util.Objects;

/**
 * Generic class used for all entities within the app that should be recognizable
 * by a uniquely generated identifier
 * @param <ID>
 *     Unique Identification token for the entity
 */
public class Entity<ID> implements Serializable {

    protected ID id;

    /**
     * Getter for the Entity's identifier
     * @return Entity's id
     */
    public ID getId() { return id; }

    /**
     * Setter for the Entity's identifier
     * @param id new id for the Entity
     */
    public void setId(ID id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }

}