package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BILLING_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SetPaymentCommand;
import seedu.address.model.person.Payment;

/**
 * Contains tests for SetPaymentCommandParser.
 */
public class SetPaymentCommandParserTest {

    private SetPaymentCommandParser parser = new SetPaymentCommandParser();

    @Test
    public void parse_validArgsStatusOnly_returnsSetPaymentCommand() {
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "PAID",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID"));
    }

    @Test
    public void parse_validArgsWithBillingDay_returnsSetPaymentCommand() {
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "15",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID", 15));
    }

    @Test
    public void parse_validArgsCaseInsensitiveStatus_returnsSetPaymentCommand() {
        // Test lowercase status
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "paid",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "paid"));

        // Test mixed case status
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "PeNdInG",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "PeNdInG"));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        // Non-numeric index
        assertParseFailure(parser, "a " + PREFIX_STATUS + "PAID", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        // Zero index
        assertParseFailure(parser, "0 " + PREFIX_STATUS + "PAID", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        // Negative index
        assertParseFailure(parser, "-1 " + PREFIX_STATUS + "PAID", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void parse_missingStatus_throwsParseException() {
        // Empty status value
        assertParseFailure(parser, "1 " + PREFIX_STATUS, Payment.MESSAGE_CONSTRAINTS_STATUS);
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        // Invalid status value
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "INVALID", Payment.MESSAGE_CONSTRAINTS_STATUS);
    }

    @Test
    public void parse_invalidBillingDay_throwsParseException() {
        // Billing day > 31
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "32",
                Payment.MESSAGE_CONSTRAINTS_DAY);

        // Billing day < 1
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "0",
                Payment.MESSAGE_CONSTRAINTS_DAY);

        // Billing day negative
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "-5",
                Payment.MESSAGE_CONSTRAINTS_DAY);

        // Non-numeric billing day
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "abc",
                Payment.MESSAGE_CONSTRAINTS_DAY);
    }

    @Test
    public void parse_emptyBillingDay_throwsParseException() {
        // Empty billing start value
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START,
                Payment.MESSAGE_CONSTRAINTS_DAY);
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        // Duplicate status prefix
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_STATUS + "PENDING",
                "Multiple values specified for the following single-valued field(s): status/");

        // Duplicate billing start prefix
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "15 "
                        + PREFIX_BILLING_START + "20",
                "Multiple values specified for the following single-valued field(s): start/");
    }

    @Test
    public void parse_validBoundaryBillingDays_returnsSetPaymentCommand() {
        // Minimum valid billing day (1)
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "1",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID", 1));

        // Maximum valid billing day (31)
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "31",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID", 31));
    }

    @Test
    public void parse_allValidStatuses_returnsSetPaymentCommand() {
        // PENDING status
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "PENDING",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "PENDING"));

        // PAID status
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "PAID",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID"));

        // OVERDUE status
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "OVERDUE",
                new SetPaymentCommand(INDEX_FIRST_PERSON, "OVERDUE"));
    }
}
