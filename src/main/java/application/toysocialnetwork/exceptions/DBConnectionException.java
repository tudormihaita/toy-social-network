package application.toysocialnetwork.exceptions;

import java.sql.SQLException;

public class DBConnectionException  extends SQLException {
    /**
     * Constructs the exception with a custom error message
     * @param message String, error message describing the invalid input provided
     */
    public DBConnectionException(String message) {
        super(message);
    }
}
