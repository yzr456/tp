package seedu.address.model.person.exceptions;

/**
 * Signals that the operation will result in overlapping sessions.
 * This exception is thrown when attempting to add a session that conflicts with an existing session
 * in the weekly schedule.
 */
public class OverlapSessionException extends RuntimeException {
    /**
     * Constructs an OverlapSessionException with a default error message.
     */
    public OverlapSessionException() {
        super("Operation would result in an overlapping session");
    }
}
