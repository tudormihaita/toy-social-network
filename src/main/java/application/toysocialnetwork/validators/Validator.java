package application.toysocialnetwork.validators;

import application.toysocialnetwork.exceptions.ValidationException;

/**
 * Generic Validator interface class providing 2 possible Validation Strategies, Quick or Exhaustive
 * @param <T> Data type of the verified entity
 */
public interface Validator<T> {
    /**
     * Method for validating the provided entity using a selected Strategy
     * @param entity T, the provided entity for validation
     * @param strategy ValidationStrategy, type of validation used
     * @throws ValidationException if the entity fails to be validated
     */
    default void  validate(T entity, ValidationStrategy strategy) throws ValidationException {
        switch (strategy) {
            case QUICK -> validateQuick(entity);
            case EXHAUSTIVE -> validateExhaustively(entity);
        }
    }

    void validateQuick(T Entity) throws ValidationException;
    void validateExhaustively(T entity) throws ValidationException;

}
