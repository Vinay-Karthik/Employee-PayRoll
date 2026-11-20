package integration;

/**
 * Exception representing communication / network failures with the External HR API.
 */
public class ExternalServiceException extends Exception {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
