package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddSubjectCommand;
import seedu.address.model.tag.Tag;

public class AddSubjectCommandParserTest {

    private final AddSubjectCommandParser parser = new AddSubjectCommandParser();

    @Test
    public void parse_validArgs_returnsAddSubjectCommand() {
        Set<Tag> expectedTags = new HashSet<>();
        expectedTags.add(new Tag("MATH"));
        AddSubjectCommand expected =
                new AddSubjectCommand(Index.fromOneBased(1), expectedTags);
        assertParseSuccess(parser, "1 sub/MATH", expected);
    }

    @Test
    public void parse_validArgsWhitespaceAndCase_returnsAddSubjectCommand() {
        Set<Tag> expectedTags = new HashSet<>();
        expectedTags.add(new Tag("CHEM"));
        AddSubjectCommand expected =
                new AddSubjectCommand(Index.fromOneBased(2), expectedTags);
        assertParseSuccess(parser, "   2   sub/   chem   ", expected);
    }

    @Test
    public void parse_missingIndexPreamble_throwsParseExceptionWithMissingIndexMessage() {
        assertParseFailure(parser, " sub/MATH", Messages.MESSAGE_MISSING_INDEX);
    }

    @Test
    public void parse_missingSubjectPrefix_throwsParseExceptionWithMissingPrefixMessage() {
        String expected = String.format(Messages.MESSAGE_MISSING_PREFIX, AddSubjectCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "1", expected);
    }

    @Test
    public void parse_emptySubjectValue_throwsParseExceptionWithConstraints() {
        assertParseFailure(parser, "1 sub/   ", AddSubjectCommand.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidIndexZeroOrNegative_throwsParseException() {
        assertParseFailure(parser, "0 sub/MATH", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-1 sub/MATH", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidIndexNonNumeric_throwsParseException() {
        assertParseFailure(parser, "abc sub/MATH", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidSubjectCode_throwsParseExceptionWithConstraints() {
        assertParseFailure(parser, "1 sub/ENGLISH", AddSubjectCommand.MESSAGE_CONSTRAINTS);
    }
}
