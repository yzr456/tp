package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Person's study year in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidStudyYear(String)}
 */
public class StudyYear {

    public static final String MESSAGE_CONSTRAINTS = "Study year should be a capitalized level followed by a number";

    public static final Map<String, Integer> STUDY_YEARS = Map.of(
            "PRI", 6,
            "SEC", 5,
            "JC", 2,
            "POLY", 3,
            "UNI", 5
    );

    /*
     * The Study Year must start with one or more
     * **uppercase letters** and immediately end with one or more **digits**
     */
    public static final Pattern STUDY_YEAR_FORMAT = Pattern.compile("(?<acadLevel>[A-Z]+)(?<number>[0-9]+)");

    public final String value;

    /**
     * Constructs a {@code StudyYear}.
     *
     * @param studyYear A valid studyYear.
     */
    public StudyYear(String studyYear) {
        requireNonNull(studyYear);
        checkArgument(isValidStudyYear(studyYear), MESSAGE_CONSTRAINTS);
        value = studyYear;
    }

    /**
     * Returns true if a given string is a valid study year.
     */
    public static boolean isValidStudyYear(String test) {
        final Matcher matcher = STUDY_YEAR_FORMAT.matcher(test);
        if (!matcher.matches()) {
            return false;
        }

        final String acadLevel = matcher.group("acadLevel");
        final String numberStr = matcher.group("number");

        final int number = Integer.parseInt(numberStr);
        return STUDY_YEARS.containsKey(acadLevel) && number >= 1 && number <= STUDY_YEARS.get(acadLevel);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudyYear)) {
            return false;
        }

        StudyYear otherStudyYear = (StudyYear) other;
        return value.equals(otherStudyYear.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
