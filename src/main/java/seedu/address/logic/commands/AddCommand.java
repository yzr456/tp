package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDY_YEAR;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_STUDY_YEAR + "STUDY_YEAR "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS \n"
            + "Command syntax: " + COMMAND_WORD + " "
            + PREFIX_NAME + "[NAME] "
            + PREFIX_STUDY_YEAR + "[STUDY_YEAR] "
            + PREFIX_PHONE + "[PHONE] "
            + PREFIX_EMAIL + "[EMAIL] "
            + PREFIX_ADDRESS + "[ADDRESS]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_STUDY_YEAR + "SEC3 "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 \n";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_DUPLICATE_PHONE = "A person with this phone number "
            + "already exists in the address book.";
    public static final String MESSAGE_DUPLICATE_EMAIL = "A person with this email address "
            + "already exists in the address book.";
    public static final String MESSAGE_DUPLICATE_PHONE_AND_EMAIL = "A person with this phone number "
            + "and email address already exists in the address book.";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        if (model.hasContact(toAdd)) {
            // Find which field(s) are duplicated
            String errorMessage = getDuplicateContactMessage(model);
            throw new CommandException(errorMessage);
        }

        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    /**
     * Determines which contact field (phone, email, or both) is duplicated in the model.
     */
    private String getDuplicateContactMessage(Model model) {
        boolean hasPhoneDuplicate = false;
        boolean hasEmailDuplicate = false;

        for (Person person : model.getAddressBook().getPersonList()) {
            if (person.hasSameNumber(toAdd)) {
                hasPhoneDuplicate = true;
            }
            if (person.hasSameEmail(toAdd)) {
                hasEmailDuplicate = true;
            }
            if (hasPhoneDuplicate && hasEmailDuplicate) {
                break; // Both found, no need to continue
            }
        }

        if (hasPhoneDuplicate && hasEmailDuplicate) {
            return MESSAGE_DUPLICATE_PHONE_AND_EMAIL;
        } else if (hasPhoneDuplicate) {
            return MESSAGE_DUPLICATE_PHONE;
        } else {
            return MESSAGE_DUPLICATE_EMAIL;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
