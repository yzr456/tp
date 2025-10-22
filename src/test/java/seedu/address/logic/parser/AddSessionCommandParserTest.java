package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DAY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_END_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_START_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DAY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_END_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_START_DESC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.model.person.Session;
import seedu.address.model.tag.Tag;

public class AddSessionCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddSessionCommand.MESSAGE_USAGE);

    private AddSessionCommandParser parser = new AddSessionCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Tag expectedTag = new Tag(new Session("MON", "1100", "1200").toString());

        assertParseSuccess(parser, "1 d/MON s/1100 e/1200",
                new AddSessionCommand(Index.fromOneBased(1), expectedTag));
    }

    @Test
    public void parse_fieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_MISSING_PARAMETER, AddSessionCommand.MESSAGE_USAGE);

        // missing index
        assertParseFailure(parser, "d/MON s/1100 e/1200", MESSAGE_INVALID_FORMAT);

        // missing day
        assertParseFailure(parser, "1 s/1100 e/1200", expectedMessage);

        // missing start
        assertParseFailure(parser, "1 d/MON e/1200", expectedMessage);

        // missing end
        assertParseFailure(parser, "1 d/MON s/1100", expectedMessage);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5 d/MON s/1100 e/1200", MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0 d/MON s/1100 e/1200", MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid day
        assertParseFailure(parser, "1" + INVALID_DAY_DESC + VALID_START_DESC
                + VALID_END_DESC, Session.MESSAGE_CONSTRAINTS);

        // invalid start
        assertParseFailure(parser, "1" + VALID_DAY_DESC + INVALID_START_DESC
                + VALID_END_DESC, Session.MESSAGE_CONSTRAINTS);

        // invalid end
        assertParseFailure(parser, "1" + VALID_DAY_DESC + VALID_START_DESC
                + INVALID_END_DESC, Session.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        String validSessionArgumentString = VALID_DAY_DESC + VALID_START_DESC + VALID_END_DESC;
        String validExpectedSessionString = "1" + validSessionArgumentString;

        // multiple day
        assertParseFailure(parser, validExpectedSessionString + VALID_DAY_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY));

        // multiple start
        assertParseFailure(parser, validExpectedSessionString + VALID_START_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START));

        // multiple end
        assertParseFailure(parser, validExpectedSessionString + VALID_END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedSessionString + VALID_DAY_DESC + VALID_START_DESC + VALID_END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY, PREFIX_START, PREFIX_END));

        // invalid value followed by valid value

        // invalid day
        assertParseFailure(parser, "1" + INVALID_DAY_DESC + validSessionArgumentString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY));

        // invalid start
        assertParseFailure(parser, "1" + INVALID_START_DESC + validSessionArgumentString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START));

        // invalid end
        assertParseFailure(parser, "1" + INVALID_END_DESC + validSessionArgumentString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END));

        // valid value followed by invalid value

        // invalid day
        assertParseFailure(parser, validExpectedSessionString + INVALID_DAY_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY));

        // invalid start
        assertParseFailure(parser, validExpectedSessionString + INVALID_START_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START));

        // invalid end
        assertParseFailure(parser, validExpectedSessionString + INVALID_END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END));
    }
}
