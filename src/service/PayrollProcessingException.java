package service;

import java.util.List;

/**
 * Exception thrown when a critical failure occurs during payroll processing.
 *
 * <p>This exception captures both the error context (failed employee IDs) and the underlying cause,
 * allowing callers to distinguish between different types of failures and potentially retry
 * or report them appropriately.
 */
public class PayrollProcessingException extends RuntimeException {

    private final List<Integer> failedEmployeeIds;

    /**
     * Creates a new PayrollProcessingException with a message and list of failed employee IDs.
     *
     * @param message             the detail message
     * @param failedEmployeeIds   list of employee IDs that failed during processing
     */
    public PayrollProcessingException(String message, List<Integer> failedEmployeeIds) {
        super(message);
        this.failedEmployeeIds = List.copyOf(failedEmployeeIds);
    }

    /**
     * Creates a new PayrollProcessingException with a message, failed employee IDs, and cause.
     *
     * @param message             the detail message
     * @param failedEmployeeIds   list of employee IDs that failed during processing
     * @param cause               the underlying cause of the exception
     */
    public PayrollProcessingException(String message, List<Integer> failedEmployeeIds, Throwable cause) {
        super(message, cause);
        this.failedEmployeeIds = List.copyOf(failedEmployeeIds);
    }

    /**
     * Returns the list of employee IDs that failed during payroll processing.
     *
     * @return unmodifiable list of failed employee IDs (may be empty)
     */
    public List<Integer> getFailedEmployeeIds() {
        return failedEmployeeIds;
    }

    @Override
    public String toString() {
        String base = super.toString();
        if (failedEmployeeIds.isEmpty()) {
            return base;
        }
        return base + " | Failed Employee IDs: " + failedEmployeeIds;
    }
}