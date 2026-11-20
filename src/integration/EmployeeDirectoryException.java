package integration;

/**
 * Domain-level runtime exception thrown when an error occurs accessing an EmployeeDirectory.
 */
public class EmployeeDirectoryException extends RuntimeException {
    public EmployeeDirectoryException(String message) {
        super(message);
    }

    public EmployeeDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
