package structural.adapter;

/**
 * Application-level runtime exception thrown when an error occurs
 * accessing or querying an EmployeeDirectory.
 *
 * Used to translate third-party / external service failures into domain-level
 * exceptions without leaking external API-specific exception types.
 */
public class EmployeeDirectoryException extends RuntimeException {
    public EmployeeDirectoryException(String message) {
        super(message);
    }

    public EmployeeDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
