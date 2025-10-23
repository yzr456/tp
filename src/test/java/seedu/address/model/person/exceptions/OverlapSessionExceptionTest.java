package seedu.address.model.person.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for OverlapSessionException.
 */
public class OverlapSessionExceptionTest {

    @Test
    public void constructor_defaultMessage_success() {
        OverlapSessionException exception = new OverlapSessionException();
        assertEquals("Operation would result in an overlapping session", exception.getMessage());
    }

    @Test
    public void throwException_caughtAsRuntimeException_success() {
        assertThrows(RuntimeException.class, () -> {
            throw new OverlapSessionException();
        });
    }

    @Test
    public void throwException_caughtAsOverlapSessionException_success() {
        assertThrows(OverlapSessionException.class, () -> {
            throw new OverlapSessionException();
        });
    }
}
