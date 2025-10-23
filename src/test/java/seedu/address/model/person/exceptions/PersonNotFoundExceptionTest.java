package seedu.address.model.person.exceptions;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for PersonNotFoundException.
 */
public class PersonNotFoundExceptionTest {

    @Test
    public void constructor_noMessage_success() {
        PersonNotFoundException exception = new PersonNotFoundException();
        // PersonNotFoundException has no default message, should be null
        assertNull(exception.getMessage());
    }

    @Test
    public void throwException_caughtAsRuntimeException_success() {
        assertThrows(RuntimeException.class, () -> {
            throw new PersonNotFoundException();
        });
    }

    @Test
    public void throwException_caughtAsPersonNotFoundException_success() {
        assertThrows(PersonNotFoundException.class, () -> {
            throw new PersonNotFoundException();
        });
    }
}
