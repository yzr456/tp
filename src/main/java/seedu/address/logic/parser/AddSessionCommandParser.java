package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;

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
            throw new ParseException(Messages.MESSAGE_ARGUMENT_ERROR);
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_DAY, PREFIX_START, PREFIX_END)) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_PARAMETER,
                    AddSessionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DAY, PREFIX_START, PREFIX_END);
        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
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

}
