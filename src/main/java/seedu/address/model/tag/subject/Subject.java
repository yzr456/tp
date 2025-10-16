package seedu.address.model.tag.subject;

/**
 * Enumerates the subject codes that may be attached to a student as subject tags.
 */
public enum Subject {
    MATH, ENG, SCI, PHY, CHEM, BIO,
    HIST, GEOG, LIT, CHI, MALAY, TAMIL,
    POA, ECONS, ART, MUSIC, COMSCI;

    /**
     * Parses a string to a Subject (case-insensitive, trims).
     * Returns null if not a valid subject.
     */
    public static Subject of(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim().toUpperCase();
        try {
            return Subject.valueOf(t);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
