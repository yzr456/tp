package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

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
 * Attaches a validated subject tag to a student.
 */
public class AddSubjectCommand extends Command {

    public static final String COMMAND_WORD = "addsubject";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add a subject tag to a student.\n"
            + "Parameters: INDEX sub/SUBJECT\n"
            + "Command syntax: addsubject [INDEX] sub/[SUBJECT]\n"
            + "Example: " + COMMAND_WORD + " 1 sub/MATH";

    public static final String MESSAGE_SUCCESS = "Added Subject Tag: %s to %s";
    public static final String MESSAGE_DUPLICATE_SUBJECT =
            "DuplicateSubjectError: Subject Tag: %s has already been assigned to %s";

    public static final String SUBJECT_MESSAGE_CONSTRAINTS =
            "Invalid subject provided. The Subject provided must be a valid subject code: "
                    + "MATH, ENG, SCI, PHY, CHEM, BIO, HIST, GEOG, LIT, CHI, MALAY, TAMIL, "
                    + "POA, ECONS, ART, MUSIC, COMSCI";

    private final Index targetIndex;
    private final Tag subjectTag;

    /**
     * @param index the index of the target person in the current list
     * @param subjectTag the subject tag to attach
     */
    public AddSubjectCommand(Index index, Tag subjectTag) {
        requireNonNull(index);
        requireNonNull(subjectTag);
        this.targetIndex = index;
        this.subjectTag = subjectTag;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());

        if (personToEdit.getTags().contains(subjectTag)) {
            throw new CommandException(String.format(
                    MESSAGE_DUPLICATE_SUBJECT, subjectTag.tagName, personToEdit.getName().fullName));
        }

        Set<Tag> newTags = new HashSet<>(personToEdit.getTags());
        newTags.add(subjectTag);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getStudyYear(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                newTags
        );

        model.setPerson(personToEdit, editedPerson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, subjectTag, editedPerson.getName().fullName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddSubjectCommand)) {
            return false;
        }

        AddSubjectCommand otherAddSubjectCommand = (AddSubjectCommand) other;
        return targetIndex.equals(otherAddSubjectCommand.targetIndex)
                && subjectTag.equals(otherAddSubjectCommand.subjectTag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("subjectTag", subjectTag)
                .toString();
    }
}
