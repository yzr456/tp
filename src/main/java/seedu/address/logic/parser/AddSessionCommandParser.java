package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;

import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddSessionCommandParser implements Parser<AddSessionCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddSessionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_DAY, PREFIX_START, PREFIX_END);

        if (argMultimap.getPreamble().isBlank()) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_INDEX,
                    AddSessionCommand.MESSAGE_USAGE));
        }

        // First prefix check: Verify at least one prefix is present before parsing index
        // This ensures we show MESSAGE_MISSING_PREFIX instead of MESSAGE_INVALID_INDEX when user provides
        // something like "addsession xyz" (no valid prefixes). It's more helpful to indicate missing
        // session parameters than to report an invalid index format.
        if (!isAnyPrefixPresent(argMultimap, PREFIX_DAY, PREFIX_START, PREFIX_END)) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_PREFIX,
                    AddSessionCommand.MESSAGE_USAGE));
        }

        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());

        // Second prefix check: Verify ALL required prefixes are present after successful index parsing
        // This catches cases like "addsession 1 d/MON s/0900" (missing e/).
        // Error priority: Missing index → Invalid index → Missing required session parameters.
        if (!arePrefixesPresent(argMultimap, PREFIX_DAY, PREFIX_START, PREFIX_END)) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_PREFIX,
                    AddSessionCommand.MESSAGE_USAGE));
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_DAY, PREFIX_START, PREFIX_END)) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_PREFIX,
                    AddSessionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DAY, PREFIX_START, PREFIX_END);

        Tag sessionTag = ParserUtil.parseSessionTag(argMultimap.getValue(PREFIX_DAY).get(),
                argMultimap.getValue(PREFIX_START).get(), argMultimap.getValue(PREFIX_END).get());

        return new AddSessionCommand(index, sessionTag);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Returns true if at least one of the prefixes contains a non-empty {@code Optional} value in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean isAnyPrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
