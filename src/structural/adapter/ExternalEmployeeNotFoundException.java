package structural.adapter;

/**
 * Checked exception thrown by the simulated external HR system
 * when an employee record is not found in the external database.
 */
public class ExternalEmployeeNotFoundException extends Exception {
    public ExternalEmployeeNotFoundException(String message) {
        super(message);
    }
}
