package seedu.address.model.person.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for SessionNotFoundException.
 */
public class SessionNotFoundExceptionTest {

    @Test
    public void constructor_defaultMessage_success() {
        SessionNotFoundException exception = new SessionNotFoundException();
        assertEquals("Session could not be found", exception.getMessage());
    }

    @Test
    public void throwException_caughtAsRuntimeException_success() {
        assertThrows(RuntimeException.class, () -> {
            throw new SessionNotFoundException();
        });
    }

    @Test
    public void throwException_caughtAsSessionNotFoundException_success() {
        assertThrows(SessionNotFoundException.class, () -> {
            throw new SessionNotFoundException();
        });
    }
}
