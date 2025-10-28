package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's year of study in the address book
 * Guarantees: immutable; is valid as declared in {@link #isValidStudyYear(String)}
 */
public class StudyYear {
    public static final String MESSAGE_CONSTRAINTS =
            "Study year should be of format [ACAD_LEVEL][NUMBER]\n"
                    + "and adhere to the following constraints:\n"
                    + String.format("%-15s %s\n", "ACAD_LEVEL", "NUMBER(range)")
                    + String.format("%-15s %s\n", "PRI", "1 - 6")
                    + String.format("%-15s %s\n", "SEC", "1 - 5")
                    + String.format("%-15s %s\n", "JC", "1 - 2")
                    + String.format("%-15s %s\n", "POLY", "1 - 3")
                    + String.format("%-15s %s", "UNI", "1 - 5");

    public static final String VALIDATION_REGEX = "^(PRI[1-6]|SEC[1-5]|JC[1-2]|POLY[1-3]|UNI[1-5])$";
    public final String value;

    /**
     * Constructs a {@code StudyYear}.
     *
     * @param studyYear A valid year of study.
     */
    public StudyYear(String studyYear) {
        requireNonNull(studyYear);
        studyYear = studyYear.toUpperCase();
        checkArgument(isValidStudyYear(studyYear), MESSAGE_CONSTRAINTS);
        value = studyYear;
    }

    /**
     * Returns true if a given string is a valid study year.
     */
    public static boolean isValidStudyYear(String test) {
        test = test.toUpperCase();
        return test.matches(VALIDATION_REGEX);
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
