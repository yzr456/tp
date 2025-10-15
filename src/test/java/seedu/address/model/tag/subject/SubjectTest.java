package seedu.address.model.tag.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    public void of_validExactCodes_returnsEnum() {
        String[] codes = {
            "MATH", "ENG", "SCI", "PHY", "CHEM", "BIO",
            "HIST", "GEOG", "LIT", "CHI", "MALAY", "TAMIL",
            "POA", "ECONS", "ART", "MUSIC", "COMSCI"
        };
        for (String code : codes) {
            assertEquals(Subject.valueOf(code), Subject.of(code), "Failed for: " + code);
        }
    }

    @Test
    public void of_caseInsensitiveInputs_returnsEnum() {
        String[] inputs = {
            "math", "eNg", "sci", "pHy", "chem", "Bio",
            "Hist", "geog", "LIT", "chi", "malay", "TAMIL",
            "poa", "ECONS", "Art", "MuSiC", "comsci"
        };
        for (String input : inputs) {
            Subject expected = Subject.valueOf(input.toUpperCase());
            assertEquals(expected, Subject.of(input), "Failed for: " + input);
        }
    }

    @Test
    public void of_inputsWithSurroundingWhitespace_trimsAndReturnsEnum() {
        String[] inputs = {
            "  MATH  ", "\tchem\n", "   GeoG", "  lit", "   TAMIL   "
        };
        for (String input : inputs) {
            Subject expected = Subject.valueOf(input.trim().toUpperCase());
            assertEquals(expected, Subject.of(input), "Failed for: [" + input + "]");
        }
    }

    @Test
    public void of_nullInput_returnsNull() {
        assertNull(Subject.of(null));
    }

    @Test
    public void of_blankOrWhitespace_returnsNull() {
        String[] blanks = {"", " ", "\t", "\n"};
        for (String blank : blanks) {
            assertNull(Subject.of(blank), "Expected null for blank: [" + blank + "]");
        }
    }

    @Test
    public void of_unknownCodes_returnsNull() {
        String[] unknowns = {
            "MATHS", "ENGLISH", "BIOLOGY", "CS", "IT", "HISTORY",
            "GEO", "CHEMISTRY", "PHYS", "ECON", "ACCOUNTING", "POA2"
        };
        for (String unknown : unknowns) {
            assertNull(Subject.of(unknown), "Expected null for: " + unknown);
        }
    }

}
