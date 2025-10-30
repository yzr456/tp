package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Person's session in the address book.
 * Guarantees: immutable; is valid as validated in {@link #validateSessionTime(String, String, String)}
 */
public class Session implements Comparable<Session> {

    public static final List<String> DAY_OF_WEEKS = List.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
    public static final int MINIMAL_DURATION = 15;
    public static final LocalTime EARLIEST_START_TIME = LocalTime.of(8, 0);
    public static final LocalTime LATEST_END_TIME = LocalTime.of(22, 0);

    public static final String MESSAGE_INVALID_CONSTRAINTS = "SessionStr must be in format <day> <start> - <end>";
    public static final String MESSAGE_DAY_CONSTRAINTS =
            "Day cannot be blank and must be one of: MON, TUE, WED, THU, FRI, SAT, SUN";
    public static final String MESSAGE_TIME_FORMAT_CONSTRAINTS =
            "Times cannot be blank and must be in HHmm (e.g., 0900, 1730) with digits only.";
    public static final String MESSAGE_TIME_RANGE_CONSTRAINTS =
            "Start time must be before end time, with a minimum duration of " + MINIMAL_DURATION
                    + " minutes and within the range of 0800 to 2200";

    /*
     * The time must be in the format {@code HHmm}.
     */
    public static final DateTimeFormatter SESSION_FORMATTER = DateTimeFormatter.ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    public final DayOfWeek dayOfWeek;
    public final LocalTime startTime;
    public final LocalTime endTime;

    /**
     * Constructs a {@code Session}. Every field must be present and not null. {@code start} must be before {@code end}
     *
     * @param day A valid day of the week.
     * @param start a valid time before {@code end}
     * @param end a valid time after {@code start}
     */
    public Session(String day, String start, String end) {
        requireAllNonNull(day, start, end);
        validateSessionTime(day, start, end);
        dayOfWeek = DayOfWeek.of(DAY_OF_WEEKS.indexOf(day) + 1);
        startTime = LocalTime.parse(start, SESSION_FORMATTER);
        endTime = LocalTime.parse(end, SESSION_FORMATTER);
    }

    /**
     * Throws exception if the given arguments are not valid for session construction.
     */
    public static void validateSessionTime(String day, String start, String end) throws IllegalArgumentException {
        if (!DAY_OF_WEEKS.contains(day)) {
            throw new IllegalArgumentException(MESSAGE_DAY_CONSTRAINTS);
        }

        LocalTime startTime;
        LocalTime endTime;

        try {
            startTime = LocalTime.parse(start, SESSION_FORMATTER);
            endTime = LocalTime.parse(end, SESSION_FORMATTER);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(MESSAGE_TIME_FORMAT_CONSTRAINTS);
        }

        if (startTime.plusMinutes(MINIMAL_DURATION).isAfter(endTime)) {
            throw new IllegalArgumentException(MESSAGE_TIME_RANGE_CONSTRAINTS);
        } else if(startTime.isBefore(EARLIEST_START_TIME) || endTime.isAfter(LATEST_END_TIME)) {
            throw new IllegalArgumentException(MESSAGE_TIME_RANGE_CONSTRAINTS);
        }
    }

    /**
     * Returns true if there is an overlap between {@code this} session and {@code other} session.
     */
    public boolean isOverlap(Session other) {
        if (!dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }

        // If this session starts earlier than the other session,
        // then this session cannot end after the other session starts.
        // Otherwise, at the time this session starts, the other session should have already ended.

        if (startTime.isBefore(other.startTime)) {
            return endTime.isAfter(other.startTime);
        } else {
            return startTime.isBefore(other.endTime);
        }
    }

    /**
     * Returns true if this session is happening on a particular day of the week.
     *
     * @param dayOfWeek a valid day of the week
     */
    public boolean isHappeningOn(String dayOfWeek) {
        return this.dayOfWeek.equals(DayOfWeek.of(DAY_OF_WEEKS.indexOf(dayOfWeek) + 1));
    }

    /**
     * Returns true if this session is happening within a particular time interval.
     *
     * @param start a valid time string
     * @param end a valid time string
     */
    public boolean isHappeningOn(String start, String end) {
        LocalTime startTime = LocalTime.parse(start, SESSION_FORMATTER);
        LocalTime endTime = LocalTime.parse(end, SESSION_FORMATTER);

        return !(this.startTime.isAfter(startTime) || this.endTime.isBefore(endTime));
    }


    /**
     * Returns true if this session is happening within a particular time interval.
     *
     * @param startTime a valid time
     * @param endTime a valid time
     */
    public boolean isHappeningOn(LocalTime startTime, LocalTime endTime) {
        if (this.startTime.isBefore(startTime)) {
            return this.endTime.isAfter(startTime);
        } else {
            return this.startTime.isBefore(endTime);
        }
    }

    /**
     * Returns true if this session is happening at a particular time.
     *
     * @param time a valid time string
     */
    public boolean isHappeningAt(String time) {
        LocalTime timeToCheck = LocalTime.parse(time, SESSION_FORMATTER);

        return !(startTime.isAfter(timeToCheck) || endTime.isBefore(timeToCheck));
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return DAY_OF_WEEKS.get(dayOfWeek.getValue() - 1) + " " + startTime.format(SESSION_FORMATTER) + " - "
                + endTime.format(SESSION_FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Session)) {
            return false;
        }

        Session otherSession = (Session) other;
        return dayOfWeek.equals(otherSession.dayOfWeek)
                && startTime.equals(otherSession.startTime)
                && endTime.equals(otherSession.endTime);
    }

    @Override
    public int compareTo(Session other) {
        if (!dayOfWeek.equals(other.dayOfWeek)) {
            return dayOfWeek.compareTo(other.dayOfWeek);
        }
        if (!startTime.equals(other.startTime)) {
            return startTime.compareTo(other.startTime);
        }
        return endTime.compareTo(other.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }
}
