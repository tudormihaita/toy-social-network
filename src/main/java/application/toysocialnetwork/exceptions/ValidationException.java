package application.toysocialnetwork.exceptions;

/**
 * Custom Exception class, used in situations when validators fail to validate a given entity
 */
public class ValidationException  extends RuntimeException {

    /**
     * Constructs a validator exception with a specific message
     * @param message String, all error messages describing the reasons of validation failure
     */
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}