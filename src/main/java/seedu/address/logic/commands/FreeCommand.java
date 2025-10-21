package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

public class FreeCommand extends Command{
    public static final String COMMAND_WORD = "free";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds the earliest free time slot with the specified duration\n"
            + "Parameters: DURATION (must be a positive integer) \n"
            + "Command syntax: free [DURATION]\n"
            + "Example: " + COMMAND_WORD + " 1\n";

    public static final String MESSAGE_FREE_TIME_FOUND = "The earliest %dhour time slot is at: $s \n";

    public final int specifiedDuration;

    public FreeCommand(int specifiedDuration) {
        this.specifiedDuration = specifiedDuration;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        return new CommandResult(String.format(MESSAGE_FREE_TIME_FOUND, specifiedDuration));
    }
}
