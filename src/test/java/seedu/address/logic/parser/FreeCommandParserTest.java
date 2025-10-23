package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FreeCommand;

/**
 * Contains unit tests for FreeCommandParser.
 */
public class FreeCommandParserTest {

    private FreeCommandParser parser = new FreeCommandParser();

    @Test
    public void parse_validArgs_returnsFreeCommand() {
        // valid duration
        assertParseSuccess(parser, "1", new FreeCommand(1));
        assertParseSuccess(parser, "5", new FreeCommand(5));
        assertParseSuccess(parser, "10", new FreeCommand(10));

        // with leading/trailing whitespace
        assertParseSuccess(parser, "  1  ", new FreeCommand(1));
        assertParseSuccess(parser, "   5   ", new FreeCommand(5));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // empty string
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));

        // whitespace only
        assertParseFailure(parser, "   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));

        // non-numeric
        assertParseFailure(parser, "abc",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));

        // negative number
        assertParseFailure(parser, "-1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));

        // zero
        assertParseFailure(parser, "0",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));

        // decimal number
        assertParseFailure(parser, "1.5",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));

        // multiple arguments
        assertParseFailure(parser, "1 2",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));

        // with extra text
        assertParseFailure(parser, "1 hours",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE));
    }
}
