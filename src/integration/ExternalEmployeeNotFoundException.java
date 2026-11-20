package integration;

/**
 * Exception thrown when an employee record is not found in the external HR system.
 */
public class ExternalEmployeeNotFoundException extends Exception {
    public ExternalEmployeeNotFoundException(String message) {
        super(message);
    }
}
