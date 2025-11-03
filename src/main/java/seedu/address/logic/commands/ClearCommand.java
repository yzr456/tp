package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Clears all entries from the address book.\n"
            + "Example: " + COMMAND_WORD + "\nNote: You will be prompted to confirm before data is deleted.\n";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";
    public static final String MESSAGE_CONFIRM = "Are you sure you want to clear all data? (y/n)";
    public static final String MESSAGE_CANCELLED = "Clear command cancelled.";
    public static final String MESSAGE_INVALID = "Invalid input. Please try 'clear' again, then enter 'y' or 'n'.";
    private static boolean awaitingConfirmation = false;

    @Override
    public CommandResult execute(Model model) {
        setAwaitingConfirmation(true);
        return new CommandResult(MESSAGE_CONFIRM);
    }

    public static boolean isAwaitingConfirmation() {
        return awaitingConfirmation;
    }

    public static void setAwaitingConfirmation(boolean awaiting) {
        awaitingConfirmation = awaiting;
    }
}
