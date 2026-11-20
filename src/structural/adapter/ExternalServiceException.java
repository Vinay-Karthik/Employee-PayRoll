package structural.adapter;

/**
 * Checked exception representing simulated infrastructure, network,
 * or communication failures with the External HR API.
 */
public class ExternalServiceException extends Exception {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
