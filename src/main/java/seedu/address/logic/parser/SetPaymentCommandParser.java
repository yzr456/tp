package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BILLING_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import seedu.address.commons.core.index.Index;
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

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetPaymentCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_STATUS, PREFIX_BILLING_START);

        if (argMultimap.getValue(PREFIX_STATUS).isEmpty()) {
            throw new ParseException("ArgumentError: Missing value for status parameter. "
                    + "Please ensure status has a non-empty value.");
        }

        String status = ParserUtil.parsePaymentStatus(argMultimap.getValue(PREFIX_STATUS).get());

        if (argMultimap.getValue(PREFIX_BILLING_START).isPresent()) {
            int billingStartDay = ParserUtil.parseBillingDay(argMultimap.getValue(PREFIX_BILLING_START).get());
            return new SetPaymentCommand(index, status, billingStartDay);
        }

        return new SetPaymentCommand(index, status);
    }
}
