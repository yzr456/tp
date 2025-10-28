package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BILLING_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Payment;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.StudyYear;

/**
 * Sets the payment status of an existing person in the address book.
 */
public class SetPaymentCommand extends Command {

    public static final String COMMAND_WORD = "setpayment";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets the payment status of the person identified "
            + "by the index number used in the displayed person list.\n"
            + "Parameters: INDEX "
            + PREFIX_STATUS + "STATUS "
            + PREFIX_BILLING_START + "DAY\n"
            + "Command syntax: " + COMMAND_WORD + " [INDEX] "
            + PREFIX_STATUS + "[STATUS] "
            + PREFIX_BILLING_START + "[DAY]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_STATUS + "PAID " + PREFIX_BILLING_START + "15";

    public static final String MESSAGE_SET_PAYMENT_SUCCESS = "Payment status updated for %1$s";

    private final Index index;
    private final String status;
    private final Integer billingStartDay;

    /**
     * Constructs a SetPaymentCommand with status only.
     * @param index of the person in the filtered person list to set payment for
     * @param status payment status to set
     */
    public SetPaymentCommand(Index index, String status) {
        requireNonNull(index);
        requireNonNull(status);

        this.index = index;
        this.status = status;
        this.billingStartDay = null;
    }

    /**
     * Constructs a SetPaymentCommand with status and billing start day.
     * @param index of the person in the filtered person list to set payment for
     * @param status payment status to set
     * @param billingStartDay billing start day to set
     */
    public SetPaymentCommand(Index index, String status, int billingStartDay) {
        requireNonNull(index);
        requireNonNull(status);

        this.index = index;
        this.status = status;
        this.billingStartDay = billingStartDay;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createPersonWithUpdatedPayment(personToEdit);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SET_PAYMENT_SUCCESS, personToEdit.getName()));
    }

    /**
     * Creates and returns a {@code Person} with updated payment information.
     * All other fields remain unchanged.
     */
    private Person createPersonWithUpdatedPayment(Person person) {
        assert person != null;

        Name name = person.getName();
        StudyYear studyYear = person.getStudyYear();
        Phone phone = person.getPhone();
        Email email = person.getEmail();
        Address address = person.getAddress();

        Payment updatedPayment = createPayment();

        return new Person(name, studyYear, phone, email, address, person.getTags(), updatedPayment);
    }

    /**
     * Creates a Payment object based on the provided status and billing start day.
     * @return Payment object with the specified status and billing start day
     */
    private Payment createPayment() {
        if (billingStartDay != null) {
            return new Payment(status, billingStartDay);
        } else {
            return new Payment(status);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SetPaymentCommand)) {
            return false;
        }

        SetPaymentCommand otherCommand = (SetPaymentCommand) other;
        boolean indexEqual = index.equals(otherCommand.index);
        boolean statusEqual = status.equals(otherCommand.status);
        boolean billingDayEqual = (billingStartDay == null && otherCommand.billingStartDay == null)
                || (billingStartDay != null && billingStartDay.equals(otherCommand.billingStartDay));

        return indexEqual && statusEqual && billingDayEqual;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("status", status)
                .add("billingStartDay", billingStartDay)
                .toString();
    }
}
