package seedu.address.model.person.exceptions;

public class OverlapSessionException extends RuntimeException {
    public OverlapSessionException() {
        super("Operation would result in an overlapping session");
    }
}
