package seedu.address.model.person.exceptions;

/**
 * Signals that the operation is unable to find the specified session.
 * This exception is thrown when attempting to perform operations on a session
 * that does not exist in the weekly schedule.
 */
public class SessionNotFoundException extends RuntimeException {
    /**
     * Constructs a SessionNotFoundException with a default error message.
     */
    public SessionNotFoundException() {
        super("Session could not be found");
    }
}
