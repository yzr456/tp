package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Handles confirmation response for clearing the address book.
 */
public class ConfirmClearCommand extends Command {
    private final String response;

    public ConfirmClearCommand(String response) {
        this.response = response;
    }

    @Override
    public CommandResult execute(Model model) {
        ClearCommand.setAwaitingConfirmation(false);
        if (response.equalsIgnoreCase("y")) {
            requireNonNull(model);
            model.setAddressBook(new AddressBook());
            return new CommandResult(ClearCommand.MESSAGE_SUCCESS);
        } else if (response.equalsIgnoreCase("n")) {
            return new CommandResult(ClearCommand.MESSAGE_CANCELLED);
        } else {
            return new CommandResult(ClearCommand.MESSAGE_INVALID);
        }
    }
}
