package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Person's sesssion in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidSession(String, String, String)}
 */
public class Session implements Comparable<Session> {

    public static final String MESSAGE_CONSTRAINTS = "day must be one of MON TUE WED THU FRI SAT SUN"
            + "start and end must be in format \"HHmm\" and start must be before end";

    public static final List<String> dayOfWeeks = List.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");

    /*
     * The time must be in the format {@code HHmm}.
     */
    public static final DateTimeFormatter sessionFormatter = DateTimeFormatter.ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    public final DayOfWeek dayOfWeek;
    public final LocalTime startTime;
    public final LocalTime endTime;

    /**
     * Constructs a {@code Session}. Every field must be present and not null. {@code start} must be before {@code end}
     *
     * @param day A valid day of week.
     * @param start a valid time before {@code end}
     * @param end a valid time after {@code start}
     */
    public Session(String day, String start, String end) {
        requireAllNonNull(day, start, end);
        checkArgument(isValidSession(day, start, end), MESSAGE_CONSTRAINTS);
        dayOfWeek = DayOfWeek.of(dayOfWeeks.indexOf(day));
        startTime = LocalTime.parse(start, sessionFormatter);
        endTime = LocalTime.parse(end, sessionFormatter);
    }

    /**
     * Returns true if the given arguments is valid for session construction.
     */
    public static boolean isValidSession(String day, String start, String end) {
        if (!dayOfWeeks.contains(day)) {
            return false;
        }

        try {
            LocalTime startTime = LocalTime.parse(start, sessionFormatter);
            LocalTime endTime = LocalTime.parse(end, sessionFormatter);

            return !startTime.plusMinutes(15).isAfter(endTime);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if there is an overlap between {@code this} session and {@code other} session.
     */
    public boolean isOverlap(Session other) {
        if (!dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }

        // If this session starts earlier than another session, then this session cannot end after other session start.
        // Otherwise, at the time this session starts, other session should have been ended.
        if (startTime.isBefore(other.startTime)) {
            return !endTime.isAfter(other.startTime);
        } else {
            return !startTime.isBefore(other.endTime);
        }
    }

    /**
     * Returns true if this session is happening on particular day of week.
     *
     * @param dayOfWeek a valid day of week
     */
    public boolean isHappeningOn(String dayOfWeek) {
        return this.dayOfWeek.equals(DayOfWeek.of(dayOfWeeks.indexOf(dayOfWeek)));
    }

    /**
     * Returns true if this session is happening at particular time.
     *
     * @param time a valid time string
     */
    public boolean isHappeningAt(String time) {
        LocalTime timeToCheck =  LocalTime.parse(time, sessionFormatter);

        return !(startTime.isAfter(timeToCheck) || endTime.isBefore(timeToCheck));
    }

    /**
     * Returns true if this session is happening on a particular time interval.
     *
     * @param start a valid time string
     * @param end a valid time string
     */
    public boolean isHappeningOn(String start, String end) {
        LocalTime startTime =  LocalTime.parse(start, sessionFormatter);
        LocalTime endTime =  LocalTime.parse(end, sessionFormatter);

        return !(this.startTime.isAfter(startTime) || this.endTime.isBefore(endTime));
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + " - " + endTime;
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
