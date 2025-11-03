package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;
import seedu.address.model.tag.SessionTag;
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
            + PREFIX_END + "END\n"
            + "Command syntax: "
            + COMMAND_WORD + " INDEX " + PREFIX_DAY + "DAY " + PREFIX_START + "START " + PREFIX_END
            + "END (START and END must be between 0800 to 2200)\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_DAY + "MON "
            + PREFIX_START + "0900 "
            + PREFIX_END + "1800\n";

    public static final String MESSAGE_ADD_SESSION_SUCCESS = "Session added successfully\n\n%1$s";
    public static final String MESSAGE_OVERLAP_SESSION =
            "Session conflict detected. This slot overlaps an existing session: \n%s\nPlease choose a different time.";

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

        Session currentSession = ((SessionTag) sessionTag).getSession();

        // Get overlaps using WeeklySessions
        Optional<Session> overlappingSession = model.getOverlappingSession(currentSession);
        if (overlappingSession.isPresent()) {
            // Reject overlap unless it's an exact duplicate for a different person
            if (!currentSession.equals(overlappingSession.get())) {
                throw new CommandException(String.format(MESSAGE_OVERLAP_SESSION,
                        overlappingSession.get().toString()));
            }
            // If exact same session, check if current person already has it
            for (Tag tag : personToEdit.getTags()) {
                if (tag.isSessionTag()) {
                    SessionTag existingSessionTag = (SessionTag) tag;
                    if (currentSession.equals(existingSessionTag.getSession())) {
                        throw new CommandException(String.format(MESSAGE_OVERLAP_SESSION,
                                currentSession.toString()));
                    }
                }
            }
        }

        Set<Tag> newTags = new HashSet<>(personToEdit.getTags());
        newTags.add(sessionTag);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getStudyYear(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                newTags,
                personToEdit.getPayment()
        );

        model.setPerson(personToEdit, editedPerson);
        model.addSession(currentSession);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ADD_SESSION_SUCCESS, Messages.format(editedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddSessionCommand)) {
            return false;
        }

        AddSessionCommand otherAddSessionCommand = (AddSessionCommand) other;
        return Objects.equals(targetIndex, otherAddSessionCommand.targetIndex)
                && Objects.equals(sessionTag, otherAddSessionCommand.sessionTag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("toAdd", sessionTag)
                .toString();
    }
}
