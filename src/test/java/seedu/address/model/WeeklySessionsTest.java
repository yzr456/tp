package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.person.Session;

public class WeeklySessionsTest {

    private WeeklySessions weeklySessions;
    private Session monMorning;
    private Session monAfternoon;
    private Session tueAllDay;
    private Session wedMorning;

    @BeforeEach
    public void setUp() {
        weeklySessions = new WeeklySessions();
        monMorning = new Session("MON", "0800", "1200");
        monAfternoon = new Session("MON", "1400", "1600");
        tueAllDay = new Session("TUE", "0800", "2200");
        wedMorning = new Session("WED", "0900", "1100");
    }

    @Test
    public void constructor_success() {
        WeeklySessions ws = new WeeklySessions();
        assertEquals("[]", ws.toString());
    }

    @Test
    public void add_singleSession_success() {
        weeklySessions.add(monMorning);
        assertTrue(weeklySessions.hasOverlap(monMorning));
    }

    @Test
    public void add_multipleNonOverlappingSessions_success() {
        weeklySessions.add(monMorning);
        weeklySessions.add(monAfternoon);
        weeklySessions.add(wedMorning);

        assertTrue(weeklySessions.hasOverlap(monMorning));
        assertTrue(weeklySessions.hasOverlap(monAfternoon));
        assertTrue(weeklySessions.hasOverlap(wedMorning));
    }

    @Test
    public void add_duplicateSessions_allowed() {
        weeklySessions.add(monMorning);
        weeklySessions.add(monMorning);
        assertTrue(weeklySessions.hasOverlap(monMorning));
    }

    @Test
    public void add_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> weeklySessions.add(null));
    }

    @Test
    public void hasOverlap_emptySchedule_returnsFalse() {
        assertFalse(weeklySessions.hasOverlap(monMorning));
    }

    @Test
    public void hasOverlap_exactMatch_returnsTrue() {
        weeklySessions.add(monMorning);
        assertTrue(weeklySessions.hasOverlap(monMorning));
    }

    @Test
    public void hasOverlap_partialOverlap_returnsTrue() {
        weeklySessions.add(monMorning);
        Session overlapping = new Session("MON", "1000", "1400");
        assertTrue(weeklySessions.hasOverlap(overlapping));
    }

    @Test
    public void hasOverlap_adjacentSessions_returnsFalse() {
        weeklySessions.add(monMorning);
        Session adjacent = new Session("MON", "1200", "1400");
        assertFalse(weeklySessions.hasOverlap(adjacent));
    }

    @Test
    public void hasOverlap_differentDay_returnsFalse() {
        weeklySessions.add(monMorning);
        Session differentDay = new Session("TUE", "0800", "1200");
        assertFalse(weeklySessions.hasOverlap(differentDay));
    }

    @Test
    public void hasOverlap_sessionEnclosed_returnsTrue() {
        weeklySessions.add(monMorning);
        Session enclosed = new Session("MON", "0900", "1100");
        assertTrue(weeklySessions.hasOverlap(enclosed));
    }

    @Test
    public void hasOverlap_sessionEncloses_returnsTrue() {
        weeklySessions.add(wedMorning);
        Session encloses = new Session("WED", "0800", "1200");
        assertTrue(weeklySessions.hasOverlap(encloses));
    }

    @Test
    public void getOverlap_noOverlap_returnsEmpty() {
        weeklySessions.add(monMorning);
        Session noOverlap = new Session("TUE", "0800", "1200");
        assertTrue(weeklySessions.getOverlap(noOverlap).isEmpty());
    }

    @Test
    public void getOverlap_hasOverlap_returnsSession() {
        weeklySessions.add(monMorning);
        Session overlapping = new Session("MON", "1000", "1400");
        assertTrue(weeklySessions.getOverlap(overlapping).isPresent());
        assertEquals(monMorning, weeklySessions.getOverlap(overlapping).get());
    }

    @Test
    public void getOverlap_multipleOverlaps_returnsFirst() {
        weeklySessions.add(monMorning);
        weeklySessions.add(monAfternoon);
        Session overlappingBoth = new Session("MON", "0800", "1600");
        assertTrue(weeklySessions.getOverlap(overlappingBoth).isPresent());
    }

    @Test
    public void remove_existingSession_success() {
        weeklySessions.add(monMorning);
        weeklySessions.remove(monMorning);
        assertFalse(weeklySessions.hasOverlap(monMorning));
    }

    @Test
    public void remove_nonExistentSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> weeklySessions.remove(monMorning));
    }

    @Test
    public void remove_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> weeklySessions.remove(null));
    }

    @Test
    public void getEarliestFreeTime_emptySchedule_returnsMonday8am() {
        String result = weeklySessions.getEarliestFreeTime(2);
        assertEquals("The earliest free time is: MONDAY 08:00", result);
    }

    @Test
    public void getEarliestFreeTime_findGapBetweenSessions() {
        weeklySessions.add(monMorning);
        weeklySessions.add(monAfternoon);
        String result = weeklySessions.getEarliestFreeTime(2);
        assertEquals("The earliest free time is: MONDAY 12:00", result);
    }

    @Test
    public void getEarliestFreeTime_mondayFull_returnsTuesday() {
        weeklySessions.add(monMorning);
        weeklySessions.add(monAfternoon);
        String result = weeklySessions.getEarliestFreeTime(11);
        assertEquals("The earliest free time is: TUESDAY 08:00", result);
    }

    @Test
    public void getEarliestFreeTime_afterExistingSession() {
        weeklySessions.add(monMorning);
        String result = weeklySessions.getEarliestFreeTime(2);
        assertEquals("The earliest free time is: MONDAY 12:00", result);
    }

    @Test
    public void getEarliestFreeTime_beforeExistingSession() {
        Session monLate = new Session("MON", "1400", "1600");
        weeklySessions.add(monLate);
        String result = weeklySessions.getEarliestFreeTime(2);
        assertEquals("The earliest free time is: MONDAY 08:00", result);
    }

    @Test
    public void getEarliestFreeTime_noFreeTime_returnsNoFreeTime() {
        weeklySessions.add(new Session("MON", "0800", "2200"));
        weeklySessions.add(new Session("TUE", "0800", "2200"));
        weeklySessions.add(new Session("WED", "0800", "2200"));
        weeklySessions.add(new Session("THU", "0800", "2200"));
        weeklySessions.add(new Session("FRI", "0800", "2200"));
        weeklySessions.add(new Session("SAT", "0800", "2200"));
        weeklySessions.add(new Session("SUN", "0800", "2200"));
        String result = weeklySessions.getEarliestFreeTime(1);
        assertEquals("No free time", result);
    }

    @Test
    public void getEarliestFreeTime_durationTooLong_returnsNoFreeTime() {
        String result = weeklySessions.getEarliestFreeTime(15);
        assertEquals("No free time", result);
    }

    @Test
    public void getEarliestFreeTime_exactlyFitsAtEndOfDay() {
        weeklySessions.add(monMorning);
        String result = weeklySessions.getEarliestFreeTime(10);
        assertEquals("The earliest free time is: MONDAY 12:00", result);
    }

    @Test
    public void getEarliestFreeTime_multipleDaysWithSessions() {
        weeklySessions.add(monMorning);
        weeklySessions.add(monAfternoon);
        weeklySessions.add(new Session("TUE", "0800", "1200"));
        String result = weeklySessions.getEarliestFreeTime(11);
        assertEquals("The earliest free time is: WEDNESDAY 08:00", result);
    }

    @Test
    public void setWeeklySessions_replaceWithNonEmpty_success() {
        weeklySessions.add(monMorning);

        WeeklySessions replacement = new WeeklySessions();
        replacement.add(tueAllDay);
        replacement.add(wedMorning);

        weeklySessions.setWeeklySessions(replacement);

        assertFalse(weeklySessions.hasOverlap(monMorning));
        assertTrue(weeklySessions.hasOverlap(tueAllDay));
        assertTrue(weeklySessions.hasOverlap(wedMorning));
    }

    @Test
    public void setWeeklySessions_replaceWithEmpty_clearsSchedule() {
        weeklySessions.add(monMorning);
        weeklySessions.add(monAfternoon);

        WeeklySessions replacement = new WeeklySessions();
        weeklySessions.setWeeklySessions(replacement);

        assertFalse(weeklySessions.hasOverlap(monMorning));
        assertFalse(weeklySessions.hasOverlap(monAfternoon));
    }

    @Test
    public void setWeeklySessions_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> weeklySessions.setWeeklySessions(null));
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        assertTrue(weeklySessions.equals(weeklySessions));
    }

    @Test
    public void equals_null_returnsFalse() {
        assertFalse(weeklySessions.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        assertFalse(weeklySessions.equals("string"));
    }

    @Test
    public void equals_sameSessions_returnsTrue() {
        weeklySessions.add(monMorning);
        weeklySessions.add(wedMorning);

        WeeklySessions other = new WeeklySessions();
        other.add(monMorning);
        other.add(wedMorning);

        assertTrue(weeklySessions.equals(other));
    }

    @Test
    public void equals_differentSessions_returnsFalse() {
        weeklySessions.add(monMorning);

        WeeklySessions other = new WeeklySessions();
        other.add(wedMorning);

        assertFalse(weeklySessions.equals(other));
    }

    @Test
    public void equals_emptySchedules_returnsTrue() {
        WeeklySessions other = new WeeklySessions();
        assertTrue(weeklySessions.equals(other));
    }

    @Test
    public void hashCode_sameSessions_sameHashCode() {
        weeklySessions.add(monMorning);

        WeeklySessions other = new WeeklySessions();
        other.add(monMorning);

        assertEquals(weeklySessions.hashCode(), other.hashCode());
    }

    @Test
    public void toString_emptySchedule_returnsEmptyBrackets() {
        assertEquals("[]", weeklySessions.toString());
    }

    @Test
    public void toString_withSessions_returnsSessionsString() {
        weeklySessions.add(monMorning);
        String result = weeklySessions.toString();
        assertTrue(result.contains("MONDAY") || result.contains("MON"));
    }
}
