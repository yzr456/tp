package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BILLING_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.SetPaymentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SetPaymentCommand object
 */
public class SetPaymentCommandParser implements Parser<SetPaymentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SetPaymentCommand
     * and returns a SetPaymentCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SetPaymentCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_STATUS, PREFIX_BILLING_START);

        if (argMultimap.getPreamble().isBlank()) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_INDEX,
                    SetPaymentCommand.MESSAGE_USAGE));
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_STATUS)) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_PREFIX,
                    SetPaymentCommand.MESSAGE_USAGE));
        }

        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_STATUS, PREFIX_BILLING_START);

        String status = ParserUtil.parsePaymentStatus(argMultimap.getValue(PREFIX_STATUS).get());

        if (argMultimap.getValue(PREFIX_BILLING_START).isPresent()) {
            int billingStartDay = ParserUtil.parseBillingDay(argMultimap.getValue(PREFIX_BILLING_START).get());
            return new SetPaymentCommand(index, status, billingStartDay);
        }

        return new SetPaymentCommand(index, status);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
