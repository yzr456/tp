package seedu.address.model.person.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for DuplicatePersonException.
 */
public class DuplicatePersonExceptionTest {

    @Test
    public void constructor_defaultMessage_success() {
        DuplicatePersonException exception = new DuplicatePersonException();
        assertEquals("Operation would result in duplicate persons", exception.getMessage());
    }

    @Test
    public void throwException_caughtAsRuntimeException_success() {
        assertThrows(RuntimeException.class, () -> {
            throw new DuplicatePersonException();
        });
    }

    @Test
    public void throwException_caughtAsDuplicatePersonException_success() {
        assertThrows(DuplicatePersonException.class, () -> {
            throw new DuplicatePersonException();
        });
    }
}
