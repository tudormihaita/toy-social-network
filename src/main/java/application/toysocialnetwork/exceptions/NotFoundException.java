package application.toysocialnetwork.exceptions;

/**
 * Custom Exception Class, specific for situations when a searched entity is not found
 * or identifier is invalid
 */
public class NotFoundException extends RuntimeException {
    /**
     * Constructs the custom exception with a specific error message
     * @param message String, all error messages describing the situation
     */
    public NotFoundException(String message) {
        super(message);
    }
}
