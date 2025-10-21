package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FreeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class FreeCommandParser implements Parser<FreeCommand>{
    public FreeCommand parse(String args) throws ParseException {
        try {
            Integer duration = ParserUtil.parseDuration(args);
            return new FreeCommand(duration);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FreeCommand.MESSAGE_USAGE), pe);
        }
    }
}
