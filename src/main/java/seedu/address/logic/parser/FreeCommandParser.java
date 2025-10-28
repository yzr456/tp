package seedu.address.logic.parser;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FreeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new FreeCommand object.
 */
public class FreeCommandParser implements Parser<FreeCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the FreeCommand
     * and returns a FreeCommand object for execution.
     *
     * @param args The user input arguments containing the duration.
     * @return A FreeCommand object with the parsed duration.
     * @throws ParseException If the user input does not conform to the expected format,
     *                        or if the duration is not a positive integer.
     */
    public FreeCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(FreeCommand.MESSAGE_MISSING_DURATION, FreeCommand.MESSAGE_USAGE));
        }

        // Check if there are multiple arguments (contains spaces)
        if (trimmedArgs.contains(" ")) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    FreeCommand.MESSAGE_USAGE));
        }

        int duration = ParserUtil.parseDuration(args);
        return new FreeCommand(duration);
    }

}
