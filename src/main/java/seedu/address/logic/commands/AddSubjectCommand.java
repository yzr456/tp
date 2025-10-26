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
 * Attaches validated subject tags to a student.
 */
public class AddSubjectCommand extends Command {

    public static final String COMMAND_WORD = "addsubject";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add subject tag(s) to a student.\n"
            + "Parameters: INDEX sub/SUBJECT...\n"
            + "Command syntax: addsubject [INDEX] sub/[SUBJECT]...\n"
            + "Example: " + COMMAND_WORD + " 1 sub/MATH\n"
            + "Example: " + COMMAND_WORD + " 3 sub/PHY sub/SCI";

    public static final String MESSAGE_SUCCESS = "Added Subject Tag(s): %s to %s";
    public static final String MESSAGE_DUPLICATE_SUBJECT = "Subject Tag(s): %s already assigned to %s";

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid subject provided. The Subject provided must be a valid subject code and cannot be blank: "
                    + "MATH, ENG, SCI, PHY, CHEM, BIO, HIST, GEOG, LIT, CHI, MALAY, TAMIL, "
                    + "POA, ECONS, ART, MUSIC, COMSCI";

    private final Index targetIndex;
    private final Set<Tag> subjectTags;

    /**
     * @param index the index of the target person in the current list
     * @param subjectTags the subject tags to attach
     */
    public AddSubjectCommand(Index index, Set<Tag> subjectTags) {
        requireNonNull(index);
        requireNonNull(subjectTags);
        this.targetIndex = index;
        this.subjectTags = subjectTags;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());

        Set<Tag> duplicates = new HashSet<>();
        for (Tag tag : subjectTags) {
            if (personToEdit.getTags().contains(tag)) {
                duplicates.add(tag);
            }
        }

        if (!duplicates.isEmpty()) {
            String duplicateNames = duplicates.stream()
                    .map(tag -> tag.tagName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new CommandException(String.format(
                    MESSAGE_DUPLICATE_SUBJECT, duplicateNames, personToEdit.getName().fullName));
        }

        Set<Tag> newTags = new HashSet<>(personToEdit.getTags());
        newTags.addAll(subjectTags);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getStudyYear(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                newTags
        );

        model.setPerson(personToEdit, editedPerson);

        String addedSubjects = subjectTags
                .stream()
                .map(tag -> tag.toString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return new CommandResult(String.format(MESSAGE_SUCCESS, addedSubjects, editedPerson.getName().fullName));
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
                && subjectTags.equals(otherAddSubjectCommand.subjectTags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("subjectTags", subjectTags)
                .toString();
    }
}
