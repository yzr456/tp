package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Adds a session to the address book.
 */
public class AddSessionCommand extends Command {

    public static final String COMMAND_WORD = "addsession";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a session to the address book.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_DAY + "DAY "
            + PREFIX_START + "START "
            + PREFIX_END + "END "
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_DAY + "MON "
            + PREFIX_START + "0900 "
            + PREFIX_END + "1800\n";

    public static final String MESSAGE_SUCCESS = "Session added successfully\n\n%1$s";
    public static final String MESSAGE_DUPLICATE_SESSION = "This session already exists in the person's tags";

    private final Index targetIndex;
    private final Tag sessionTag;

    /**
     * Creates an AddSessionCommand to add the specified {@code Session}
     */
    public AddSessionCommand(Index index, Tag sessionTag) {
        requireAllNonNull(index, sessionTag);
        targetIndex = index;
        this.sessionTag = sessionTag;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());

        if (personToEdit.getTags().contains(sessionTag)) {
            throw new CommandException(String.format(
                    MESSAGE_DUPLICATE_SESSION, sessionTag.tagName, personToEdit.getName().fullName));
        }

        Set<Tag> newTags = new HashSet<>(personToEdit.getTags());
        newTags.add(sessionTag);

        newTags.add(sessionTag);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getStudyYear(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags()
        );

        model.setPerson(personToEdit, editedPerson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(editedPerson)));
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

        AddSessionCommand otherAddCommand = (AddSessionCommand) other;
        return sessionTag.equals(otherAddCommand.sessionTag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("toAdd", sessionTag)
                .toString();
    }
}
