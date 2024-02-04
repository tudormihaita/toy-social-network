package application.toysocialnetwork.exceptions;

/**
 * Custom Exception Class, specific for situations when user input fails to be parsed
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Constructs the exception with a custom error message
     * @param message String, error message describing the invalid input provided
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
