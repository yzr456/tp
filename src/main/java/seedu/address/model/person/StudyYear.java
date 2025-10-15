package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 *  Represent a Person's year of study in the address book
 *  Guarantees: immutable; is valid as declared in {@link #isValidStudyYear(String)}
 */
public class StudyYear {
    public static final String MESSAGE_CONSTRAINTS =
            "Study year should be of format {ACAD_LEVEL}{NUMBER} "
            + "and adhere to the following constraints:\n"
            + "ACAD_LEVEL           NUMBER(range)"
            + " PRI                 1 - 6"
            + " SEC                 1 - 5"
            + " JC                  1 - 2"
            + " POLY                1 - 3"
            + " UNI                 1 - 5\n";

    public static final String VALIDATION_REGEX = "^(PRI[1-6]|SEC[1-5]|JC[1-2]|POLY[1-3]|UNI[1-5])$";
    public final String studyYear;

    /**
     * Constructs a {@code StudyYear}.
     *
     * @param studyYear A valid year of study
     */
    public StudyYear(String studyYear) {
        requireNonNull(studyYear);
        checkArgument(isValidStudyYear(studyYear), MESSAGE_CONSTRAINTS);
        this.studyYear = studyYear;
    }

    public static boolean isValidStudyYear(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return studyYear;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone)) {
            return false;
        }

        StudyYear otherStudyYear = (StudyYear) other;
        return studyYear.equals(otherStudyYear.studyYear);
    }

    @Override
    public int hashCode() {
        return studyYear.hashCode();
    }

}
