package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudyYearTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudyYear(null));
    }

    @Test
    public void constructor_invalidStudyYear_throwsIllegalArgumentException() {
        String invalidStudyYear = "";
        assertThrows(IllegalArgumentException.class, () -> new StudyYear(invalidStudyYear));
    }

    @Test
    public void isValidStudyYear() {
        // null studyYear
        assertThrows(NullPointerException.class, () -> StudyYear.isValidStudyYear(null));

        // invalid studyYears
        assertFalse(StudyYear.isValidStudyYear("")); // empty string
        assertFalse(StudyYear.isValidStudyYear(" ")); // spaces only
        assertFalse(StudyYear.isValidStudyYear("SENIOR1")); // invalid academic level
        assertFalse(StudyYear.isValidStudyYear("SEC")); // no number provided
        assertFalse(StudyYear.isValidStudyYear("JC 2")); // spaces in between
        assertFalse(StudyYear.isValidStudyYear("POLY7")); // number out of range

        // valid studyYears
        assertTrue(StudyYear.isValidStudyYear("PRI6"));
        assertTrue(StudyYear.isValidStudyYear("JC2"));
        assertTrue(StudyYear.isValidStudyYear("UNI1"));
        assertTrue(StudyYear.isValidStudyYear("pri1"));
    }

    @Test
    public void equals() {
        StudyYear studyYear = new StudyYear("UNI5");

        // same values -> returns true
        assertTrue(studyYear.equals(new StudyYear("UNI5")));

        // same object -> returns true
        assertTrue(studyYear.equals(studyYear));

        // null -> returns false
        assertFalse(studyYear.equals(null));

        // different types -> returns false
        assertFalse(studyYear.equals(5.0f));

        // different values -> returns false
        assertFalse(studyYear.equals(new StudyYear("SEC5")));
    }
}
