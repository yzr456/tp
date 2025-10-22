package seedu.address.model;


import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.TreeSet;

import seedu.address.model.person.Session;
import seedu.address.model.person.exceptions.OverlapSessionException;


public class WeeklySessions {
    private static final LocalTime EARLIEST_START = LocalTime.of(8, 0);
    private static final LocalTime LATEST_END = LocalTime.of(22, 0);
    private static final DayOfWeek START_OF_WEEK = DayOfWeek.MONDAY;
    private static final DayOfWeek END_OF_WEEK = DayOfWeek.SUNDAY;

    private TreeSet<Session> weeklySessions;

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

    public WeeklySessions() {
        weeklySessions = new TreeSet<>(new SessionComparator());
    }

    public boolean hasOverlap(Session sessionToCheck) {
        return weeklySessions.stream()
                .anyMatch(session -> session.isOverlap(sessionToCheck));
    }

    public void add(Session session) {
        requireNonNull(session);
        if (hasOverlap(session)) {
            throw new OverlapSessionException();
        }
        weeklySessions.add(session);
    }

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

    private boolean fitsInDay(LocalTime start, LocalTime end) {
        return !end.isAfter(LATEST_END) && !end.isBefore(start);
    }

    private String formatResult(DayOfWeek day, LocalTime time) {
        return String.format("The earliest free time is: %s %s", day, time);
    }
}
