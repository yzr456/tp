package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.WeeklySessions;

/**
 * Finds the earliest available free time slot in the weekly schedule.
 * The command searches for a continuous time slot that can accommodate the specified duration,
 * starting from Monday 08:00 and checking through Sunday 22:00.
 */
public class FreeCommand extends Command {
    public static final String COMMAND_WORD = "free";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds the earliest free time slot with the specified duration\n"
            + "Parameters: DURATION (must be a positive integer) \n"
            + "Command syntax: free [DURATION]\n"
            + "Example: " + COMMAND_WORD + " 1\n";

    public static final String MESSAGE_FREE_TIME_FOUND = "The earliest %d hour time slot is at: %s \n";

    public final int specifiedDuration;

    /**
     * Creates a FreeCommand to find the earliest free time slot.
     *
     * @param specifiedDuration The duration in hours for the required free time slot.
     */
    public FreeCommand(int specifiedDuration) {
        this.specifiedDuration = specifiedDuration;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        WeeklySessions weeklySessions = model.getWeeklySessions();
        String earliestTimeFrame = weeklySessions.getEarliestFreeTime(specifiedDuration);

        return new CommandResult(String.format(MESSAGE_FREE_TIME_FOUND, specifiedDuration, earliestTimeFrame));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FreeCommand)) {
            return false;
        }

        FreeCommand otherFreeCommand = (FreeCommand) other;
        return specifiedDuration == otherFreeCommand.specifiedDuration;
    }

    @Override
    public String toString() {
        return FreeCommand.class.getCanonicalName() + "{specifiedDuration=" + specifiedDuration + "}";
    }
}
