package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class SessionTest {
    private final Session session = new Session("MON", "1100", "1200");

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Session(null, null, null));
    }

    @Test
    public void constructor_invalidSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Session("ABC", "1170", "12345"));
    }

    @Test
    public void constructor_validSession_success() {
        assertEquals(DayOfWeek.valueOf("MONDAY"), session.dayOfWeek);
        assertEquals(LocalTime.of(11, 0), session.startTime);
        assertEquals(LocalTime.of(12, 0), session.endTime);
        assertEquals("MON 1100 - 1200", session.toString());
    }

    @Test
    public void isValidSession() {
        // null session number
        assertThrows(NullPointerException.class, () -> Session.validateSessionTime(null, null, null));

        // invalid session
        assertThrows(IllegalArgumentException.class, () ->
                Session.validateSessionTime("Monday", "1100", "1200")); // invalid day of week
        assertThrows(IllegalArgumentException.class, () ->
                Session.validateSessionTime("MON", "11:00", "12:00")); // wrong time format
        assertThrows(IllegalArgumentException.class, () ->
                Session.validateSessionTime("MON", "1170", "2400")); // invalid time
        assertThrows(IllegalArgumentException.class, () ->
                Session.validateSessionTime("MON", "1100", "1101")); // session too short
        assertThrows(IllegalArgumentException.class, () ->
                Session.validateSessionTime("MON", "1100", "0900")); // end time after start time
        assertThrows(IllegalArgumentException.class, () ->
                Session.validateSessionTime("MON", "0600", "0900")); //start time before earliest start time
        assertThrows(IllegalArgumentException.class, () ->
                Session.validateSessionTime("MON", "0800", "2300")); //end time after latest end time

        // valid session
        assertDoesNotThrow(() -> Session.validateSessionTime("MON", "1100", "1200"));
        assertDoesNotThrow(() -> Session.validateSessionTime("SAT", "1500", "1600"));

    }

    @Test
    public void isOverlap() {
        assertFalse(session.isOverlap(new Session("TUE", "1100", "1200"))); // different day
        assertFalse(session.isOverlap(new Session("MON", "1200", "1300")));
        assertFalse(session.isOverlap(new Session("MON", "0900", "1030")));

        assertTrue(session.isOverlap(new Session("MON", "1130", "1230")));
        assertTrue(session.isOverlap(new Session("MON", "0900", "1130")));
        assertTrue(session.isOverlap(new Session("MON", "1159", "1215")));
    }

    @Test
    public void isHappeningOn() {
        assertFalse(session.isHappeningOn("SUN"));
        assertFalse(session.isHappeningOn("1300", "1400"));
        assertFalse(session.isHappeningOn("1200", "1300"));
        assertFalse(session.isHappeningOn("1030", "1130"));
        assertFalse(session.isHappeningOn("1100", "1201"));

        assertTrue(session.isHappeningOn("MON"));
        assertTrue(session.isHappeningOn("1115", "1145"));
        assertTrue(session.isHappeningOn("1100", "1200"));
    }

    @Test
    public void isHappeningAt() {
        assertFalse(session.isHappeningAt("1000"));
        assertFalse(session.isHappeningAt("1059"));

        assertTrue(session.isHappeningAt("1130"));
        assertTrue(session.isHappeningAt("1100"));
        assertTrue(session.isHappeningAt("1200"));
    }

    @Test
    public void compareTo() {
        assertEquals(0, session.compareTo(session));
        assertEquals(0, session.compareTo(new Session("MON", "1100", "1200")));

        assertTrue(session.compareTo(new Session("TUE", "1100", "1200")) < 0);
        assertTrue(session.compareTo(new Session("MON", "1000", "1200")) > 0);
        assertTrue(session.compareTo(new Session("MON", "1100", "1300")) < 0);
    }
}
