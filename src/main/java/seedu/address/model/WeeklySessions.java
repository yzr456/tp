package seedu.address.model;


import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

import seedu.address.model.person.Session;
import seedu.address.model.person.exceptions.OverlapSessionException;

/**
 * Represents a weekly schedule that manages sessions across a week.
 * Guarantees: Sessions do not overlap; sessions are sorted by day and time.
 * The week is defined from Monday to Sunday, with available time slots from 08:00 to 22:00.
 */
public class WeeklySessions {
    /** The earliest time a session can start in a day. */
    private static final LocalTime EARLIEST_START = LocalTime.of(8, 0);

    /** The latest time a session can end in a day. */
    private static final LocalTime LATEST_END = LocalTime.of(22, 0);

    /** The first day of the week. */
    private static final DayOfWeek START_OF_WEEK = DayOfWeek.MONDAY;

    /** The last day of the week. */
    private static final DayOfWeek END_OF_WEEK = DayOfWeek.SUNDAY;

    private TreeSet<Session> weeklySessions;

    /**
     * Comparator for ordering sessions by day of week and time.
     */
    private static class SessionComparator implements Comparator<Session> {
        @Override
        public int compare(Session s1, Session s2) {
            if (s1.compareTo(s2) < 0) {
                return -1;
            } else if (s1.compareTo(s2) > 0) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Constructs an empty WeeklySessions with no scheduled sessions.
     */
    public WeeklySessions() {
        weeklySessions = new TreeSet<>(new SessionComparator());
    }

    /**
     * Returns true if the given session overlaps with any existing session in the weekly schedule.
     *
     * @param sessionToCheck The session to check for overlap.
     * @return true if there is an overlap, false otherwise.
     */
    public boolean hasOverlap(Session sessionToCheck) {
        return weeklySessions.stream()
                .anyMatch(session -> session.isOverlap(sessionToCheck));
    }

    public Optional<Session> getOverlap(Session sessionToCheck) {
        return weeklySessions.stream()
                .filter(session -> session.isOverlap(sessionToCheck))
                .findFirst();
    }

    /**
     * Adds a session to the weekly schedule.
     * The session must not overlap with any existing session.
     *
     * @param session The session to add.
     * @throws OverlapSessionException if the session overlaps with an existing session.
     */
    public void add(Session session) {
        requireNonNull(session);
        if (hasOverlap(session)) {
            throw new OverlapSessionException();
        }
        weeklySessions.add(session);
    }

    /**
     * Finds the earliest available time slot in the week that can accommodate the given duration.
     * Searches from Monday 08:00 onwards, checking for conflicts with existing sessions.
     *
     * @param duration The required duration in hours.
     * @return A formatted string indicating the earliest free time slot, or "No free time" if none available.
     */
    public String getEarliestFreeTime(int duration) {
        DayOfWeek currentDay = START_OF_WEEK;
        LocalTime candidateStart = EARLIEST_START;
        LocalTime candidateEnd = candidateStart.plusHours(duration);

        for (Session session : weeklySessions) {
            // Found free slot before this session
            if (session.getDayOfWeek().getValue() > currentDay.getValue()) {
                break;
            }

            // Conflict - move candidate after this session
            if (session.isHappeningOn(candidateStart, candidateEnd)) {
                candidateStart = session.getEndTime();
                candidateEnd = candidateStart.plusHours(duration);

                // Doesn't fit today - try next day
                while (!fitsInDay(candidateStart, candidateEnd)) {
                    if (currentDay.equals(END_OF_WEEK)) {
                        return "No free time";
                    }
                    currentDay = currentDay.plus(1);
                    candidateStart = EARLIEST_START;
                    candidateEnd = candidateStart.plusHours(duration);
                }
            }
        }

        // Validate and adjust final candidate
        while (!fitsInDay(candidateStart, candidateEnd)) {
            if (currentDay.equals(END_OF_WEEK)) {
                return "No free time";
            }
            currentDay = currentDay.plus(1);
            candidateStart = EARLIEST_START;
            candidateEnd = candidateStart.plusHours(duration);
        }

        return formatResult(currentDay, candidateStart);
    }

    /**
     * Checks if a time slot fits within a single day's available hours.
     * A slot fits if it doesn't exceed the latest end time (22:00) and doesn't wrap around midnight.
     *
     * @param start The start time of the slot.
     * @param end The end time of the slot.
     * @return true if the slot fits within the day, false otherwise.
     */
    private boolean fitsInDay(LocalTime start, LocalTime end) {
        return !end.isAfter(LATEST_END) && !end.isBefore(start);
    }

    /**
     * Formats the result of finding a free time slot.
     *
     * @param day The day of the week.
     * @param time The start time.
     * @return A formatted string describing the free time slot.
     */
    private String formatResult(DayOfWeek day, LocalTime time) {
        return String.format("The earliest free time is: %s %s", day, time);
    }

    /**
     * Replaces the contents of this weekly sessions with {@code replacement}.
     *
     * @param replacement The WeeklySessions to copy from.
     */
    public void setWeeklySessions(WeeklySessions replacement) {
        requireNonNull(replacement);
        weeklySessions.clear();
        weeklySessions.addAll(replacement.weeklySessions);
    }

    /**
     * Removes a session from the weekly sessions.
     * The session must exist in the weekly sessions.
     *
     * @param session The session to remove.
     * @throws IllegalArgumentException if the session does not exist in the weekly sessions.
     */
    public void remove(Session session) {
        requireNonNull(session);
        if (!weeklySessions.remove(session)) {
            throw new IllegalArgumentException("Session does not exist in weekly sessions");
        }
    }

    /**
     * Returns true if both WeeklySessions have the same set of sessions.
     * This defines a weaker notion of equality between two WeeklySessions.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof WeeklySessions)) {
            return false;
        }

        WeeklySessions otherWeeklySessions = (WeeklySessions) other;
        return weeklySessions.equals(otherWeeklySessions.weeklySessions);
    }

    @Override
    public int hashCode() {
        return weeklySessions.hashCode();
    }

    @Override
    public String toString() {
        return weeklySessions.toString();
    }
}
