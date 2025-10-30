package seedu.address.model;


import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import seedu.address.model.person.Session;

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
    private Map<Session, Integer> sessionCounts;

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
        sessionCounts = new HashMap<>();
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
     * Note: Duplicate sessions (same time) are allowed since different people can have sessions at the same time.
     * Overlap checking should be done at the command level.
     *
     * @param session The session to add.
     */
    public void add(Session session) {
        requireNonNull(session);
        weeklySessions.add(session);
        if (!sessionCounts.containsKey(session)) {
            sessionCounts.put(session, 1);
        } else {
            sessionCounts.put(session, sessionCounts.get(session) + 1);
        }
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

        while (currentDay.getValue() <= END_OF_WEEK.getValue()) {
            LocalTime candidateStart = EARLIEST_START;
            LocalTime candidateEnd = candidateStart.plusHours(duration);

            for (Session session : weeklySessions) {
                // Sessions are sorted - if we hit a later day, we're done with current day
                if (session.getDayOfWeek().getValue() > currentDay.getValue()) {
                    break;
                }

                // Only process sessions on the current day
                if (session.getDayOfWeek().equals(currentDay)) {
                    // Check if candidate fits before this session
                    if (!candidateEnd.isAfter(session.getStartTime())) {
                        break;
                    }

                    // Move candidate after this session
                    candidateStart = session.getEndTime();
                    candidateEnd = candidateStart.plusHours(duration);
                }
            }

            // Check if final candidate fits in the day
            if (fitsInDay(candidateStart, candidateEnd)) {
                return formatResult(currentDay, candidateStart);
            }

            // Try next day
            if (currentDay.equals(END_OF_WEEK)) {
                return "No free time";
            }
            currentDay = currentDay.plus(1);
        }

        return "No free time";
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
    public void set(WeeklySessions replacement) {
        requireNonNull(replacement);
        weeklySessions.clear();
        weeklySessions.addAll(replacement.weeklySessions);
        sessionCounts.clear();
        sessionCounts.putAll(replacement.sessionCounts);
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
        if (!sessionCounts.containsKey(session)) {
            throw new IllegalArgumentException("Session does not exist in weekly sessions");
        } else {
            int participantCount = sessionCounts.get(session);
            if (participantCount - 1 == 0) {
                weeklySessions.remove(session);
                sessionCounts.remove(session); // Remove from map when count reaches 0
            } else {
                sessionCounts.put(session, sessionCounts.get(session) - 1);
            }
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
