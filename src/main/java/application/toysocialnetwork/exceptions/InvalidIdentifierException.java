package application.toysocialnetwork.exceptions;

/**
 * Custom Exception Class, specific for situations when a provided entity identifier doesn't exist,
 * or a duplicate identifier is found
 */
public class InvalidIdentifierException extends IllegalArgumentException {

    /**
     * Constructs the custom exception with a specific error message
     * @param message String, all error messages describing the situation
     */
    public InvalidIdentifierException(String message) {
        super(message);
    }
}
