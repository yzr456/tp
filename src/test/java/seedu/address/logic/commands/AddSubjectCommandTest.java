package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SUBJECT_TAG;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code AddSubjectCommand}.
 */
public class AddSubjectCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag subjectTag = new Tag(VALID_SUBJECT_TAG);
        AddSubjectCommand addSubjectCommand = new AddSubjectCommand(INDEX_FIRST_PERSON, subjectTag);

        String expectedMessage = String.format(AddSubjectCommand.MESSAGE_SUCCESS,
                subjectTag, personToEdit.getName().fullName);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Person editedPerson = withTag(personToEdit, subjectTag);
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(addSubjectCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AddSubjectCommand addSubjectCommand = new AddSubjectCommand(outOfBoundIndex, new Tag(VALID_SUBJECT_TAG));

        assertCommandFailure(addSubjectCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag subjectTag = new Tag(VALID_SUBJECT_TAG);
        AddSubjectCommand addSubjectCommand = new AddSubjectCommand(INDEX_FIRST_PERSON, subjectTag);

        String expectedMessage = String.format(AddSubjectCommand.MESSAGE_SUCCESS,
                subjectTag, personInFilteredList.getName().fullName);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Person editedPerson = withTag(personInFilteredList, subjectTag);
        // update the person in the expected model
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(addSubjectCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        AddSubjectCommand addSubjectCommand = new AddSubjectCommand(outOfBoundIndex, new Tag(VALID_SUBJECT_TAG));

        assertCommandFailure(addSubjectCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_duplicateSubjectUnfilteredList_failure() {
        // Ensure first person already has MATH tag
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person withMath = withTag(original, new Tag(VALID_SUBJECT_TAG));
        model.setPerson(original, withMath);

        AddSubjectCommand command = new AddSubjectCommand(INDEX_FIRST_PERSON, new Tag(VALID_SUBJECT_TAG));

        String expected = String.format(AddSubjectCommand.MESSAGE_DUPLICATE_SUBJECT,
                VALID_SUBJECT_TAG, withMath.getName().fullName);
        assertCommandFailure(command, model, expected);
    }

    @Test
    public void execute_duplicateSubjectFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person shown = model.getFilteredPersonList().get(0);
        model.setPerson(shown, withTag(shown, new Tag(VALID_SUBJECT_TAG)));

        AddSubjectCommand cmd = new AddSubjectCommand(INDEX_FIRST_PERSON, new Tag(VALID_SUBJECT_TAG));
        String expected = String.format(AddSubjectCommand.MESSAGE_DUPLICATE_SUBJECT,
                VALID_SUBJECT_TAG, shown.getName().fullName);

        assertCommandFailure(cmd, model, expected);
    }

    @Test
    public void execute_addSubjectPreservesExistingTags_success() {
        Person base = new PersonBuilder().withTags("FRIEND", "TEAMMATE").build();
        model.setPerson(model.getFilteredPersonList().get(0), base);

        Tag subjectTag = new Tag(VALID_SUBJECT_TAG);
        AddSubjectCommand cmd = new AddSubjectCommand(INDEX_FIRST_PERSON, subjectTag);

        Person expectedEdited = new PersonBuilder(base).withTags("FRIEND", "TEAMMATE", VALID_SUBJECT_TAG).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(expectedModel.getFilteredPersonList().get(0), expectedEdited);

        String expectedMsg = String.format(AddSubjectCommand.MESSAGE_SUCCESS,
                subjectTag, base.getName().fullName);

        assertCommandSuccess(cmd, model, expectedMsg, expectedModel);
    }

    @Test
    public void equals() {
        AddSubjectCommand addFirstMath = new AddSubjectCommand(INDEX_FIRST_PERSON, new Tag(VALID_SUBJECT_TAG));
        AddSubjectCommand addSecondMath = new AddSubjectCommand(INDEX_SECOND_PERSON, new Tag(VALID_SUBJECT_TAG));

        // same object -> true
        assertTrue(addFirstMath.equals(addFirstMath));

        // same values -> true
        AddSubjectCommand addFirstMathCopy = new AddSubjectCommand(INDEX_FIRST_PERSON, new Tag(VALID_SUBJECT_TAG));
        assertTrue(addFirstMath.equals(addFirstMathCopy));

        // different types -> false
        assertFalse(addFirstMath.equals(1));

        // null -> false
        assertFalse(addFirstMath.equals(null));

        // different index -> false
        assertFalse(addFirstMath.equals(addSecondMath));

        // different tag -> false
        AddSubjectCommand addFirstEng = new AddSubjectCommand(INDEX_FIRST_PERSON, new Tag("ENG"));
        assertFalse(addFirstMath.equals(addFirstEng));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        Tag subjectTag = new Tag(VALID_SUBJECT_TAG);
        AddSubjectCommand cmd = new AddSubjectCommand(targetIndex, subjectTag);
        String expected = AddSubjectCommand.class.getCanonicalName()
                + "{targetIndex=" + targetIndex + ", subjectTag=" + subjectTag + "}";
        assertEquals(expected, cmd.toString());
    }

    /**
     * Returns a new {@code Person} that contains all existing tags of {@code base} plus {@code extra} (if not present).
     */
    private Person withTag(Person base, Tag extra) {
        List<String> names = new ArrayList<>(base.getTags().stream()
                .map(t -> t.tagName)
                .collect(Collectors.toList()));
        if (names.stream().noneMatch(n -> n.equals(extra.tagName))) {
            names.add(extra.tagName);
        }
        return new PersonBuilder(base).withTags(names.toArray(new String[0])).build();
    }

}
