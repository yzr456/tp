package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.SessionTag;
import seedu.address.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) and unit tests for AddSessionCommand.
 */
public class AddSessionCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private final String validSessionTag = "MON 1100 - 1200";
    private final String overlappingTag = "MON 1130 - 1230";

    /**
     * Helper method to create a session tag from day, start, and end time.
     * Wraps ParserUtil to decouple tests from SessionTag implementation details.
     */
    private Tag createSessionTag(String day, String start, String end) {
        try {
            return ParserUtil.parseSessionTag(day, start, end);
        } catch (ParseException e) {
            throw new AssertionError("Test setup failed: invalid session parameters", e);
        }
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddSessionCommand(Index.fromOneBased(1), null));
        assertThrows(NullPointerException.class, () -> new AddSessionCommand(null,
                createSessionTag("MON", "1100", "1200")));
    }

    @Test
    public void execute_unfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        Tag tag = createSessionTag("MON", "1100", "1200");

        Set<Tag> newTags = new HashSet<>(lastPerson.getTags());
        newTags.add(tag);

        Person editedPerson = new Person(
                lastPerson.getName(),
                lastPerson.getStudyYear(),
                lastPerson.getPhone(),
                lastPerson.getEmail(),
                lastPerson.getAddress(),
                newTags
        );

        AddSessionCommand addSessionCommand = new AddSessionCommand(indexLastPerson, tag);

        String expectedMessage = String.format(AddSessionCommand.MESSAGE_ADD_SESSION_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);
        expectedModel.addSession(((SessionTag) tag).getSession());

        assertCommandSuccess(addSessionCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");

        Set<Tag> newTags = new HashSet<>(personInFilteredList.getTags());
        newTags.add(tag);

        Person editedPerson = new Person(
                personInFilteredList.getName(),
                personInFilteredList.getStudyYear(),
                personInFilteredList.getPhone(),
                personInFilteredList.getEmail(),
                personInFilteredList.getAddress(),
                newTags
        );
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);

        String expectedMessage = String.format(AddSessionCommand.MESSAGE_ADD_SESSION_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);
        expectedModel.addSession(((SessionTag) tag).getSession());

        assertCommandSuccess(addSessionCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_overlapSessionSamePersonUnfilteredList_failure() {
        Person personInConflict = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");
        Tag otherTag = createSessionTag("MON", "1130", "1230");
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        AddSessionCommand addOtherSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, otherTag);

        // The success path is already tested elsewhere; this execution only prepares the model for
        // the overlap-session check.
        try {
            addOtherSessionCommand.execute(model);
        } catch (Exception e) {
            fail("Unexpected exception thrown during setup: " + e.getMessage());
        }

        assertCommandFailure(addSessionCommand, model,
                String.format(AddSessionCommand.MESSAGE_OVERLAP_SESSION, otherTag.tagName));
    }

    @Test
    public void execute_overlapSessionSamePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list to have overlap session tag in address book
        Person personInConflict = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");
        Tag otherTag = createSessionTag("MON", "1130", "1230");
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        AddSessionCommand addOtherSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, otherTag);

        // The success path is already tested elsewhere; this execution only prepares the model for
        // the overlap-session check.
        try {
            addOtherSessionCommand.execute(model);
        } catch (Exception e) {
            fail("Unexpected exception thrown during setup: " + e.getMessage());
        }

        assertCommandFailure(addSessionCommand, model,
                String.format(AddSessionCommand.MESSAGE_OVERLAP_SESSION, otherTag.tagName));
    }

    @Test
    public void execute_overlapSessionDiffPersonUnfilteredList_failure() {
        Person personInConflict = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");
        Tag otherTag = createSessionTag("MON", "1130", "1230");
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        AddSessionCommand addOtherSessionCommand = new AddSessionCommand(INDEX_SECOND_PERSON, otherTag);

        // The success path is already tested elsewhere; this execution only prepares the model for
        // the overlap-session check.
        try {
            addOtherSessionCommand.execute(model);
        } catch (Exception e) {
            fail("Unexpected exception thrown during setup: " + e.getMessage());
        }

        assertCommandFailure(addSessionCommand, model,
                String.format(AddSessionCommand.MESSAGE_OVERLAP_SESSION, otherTag.tagName));
    }

    @Test
    public void execute_overlapSessionDiffPersonFilteredList_failure() {
        // edit person in filtered list to have overlap session tag in address book
        Person personInConflict = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");
        Tag otherTag = createSessionTag("MON", "1130", "1230");
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        AddSessionCommand addOtherSessionCommand = new AddSessionCommand(INDEX_SECOND_PERSON, otherTag);

        // Add session to another person and use filter to "hide" it, the command should be able to
        // find out overlap session in hidden list
        try {
            addOtherSessionCommand.execute(model);
            showPersonAtIndex(model, INDEX_FIRST_PERSON);
        } catch (Exception e) {
            fail("Unexpected exception thrown during setup: " + e.getMessage());
        }

        assertCommandFailure(addSessionCommand, model,
                String.format(AddSessionCommand.MESSAGE_OVERLAP_SESSION, otherTag.tagName));
    }

    @Test
    public void execute_duplicateSessionSamePersonUnfilteredList_failure() {
        Person personInConflict = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        AddSessionCommand addOtherSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);

        // The success path is already tested elsewhere; this execution only prepares the model for
        // the overlap-session check.
        try {
            addOtherSessionCommand.execute(model);
        } catch (Exception e) {
            fail("Unexpected exception thrown during setup: " + e.getMessage());
        }

        assertCommandFailure(addSessionCommand, model,
                String.format(AddSessionCommand.MESSAGE_OVERLAP_SESSION, tag.tagName));
    }

    @Test
    public void execute_duplicateSessionSamePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list to have overlap session tag in address book
        Person personInConflict = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        AddSessionCommand addOtherSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);

        // The success path is already tested elsewhere; this execution only prepares the model for
        // the overlap-session check.
        try {
            addOtherSessionCommand.execute(model);
        } catch (Exception e) {
            fail("Unexpected exception thrown during setup: " + e.getMessage());
        }

        assertCommandFailure(addSessionCommand, model,
                String.format(AddSessionCommand.MESSAGE_OVERLAP_SESSION, tag.tagName));
    }

    @Test
    public void execute_sameSessionDiffPersonUnfilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        AddSessionCommand addOtherSessionCommand = new AddSessionCommand(INDEX_SECOND_PERSON, tag);

        // The success path is already tested elsewhere; this execution only prepares the model for
        // the overlap-session check.
        try {
            addOtherSessionCommand.execute(model);
        } catch (Exception e) {
            fail("Unexpected exception thrown during setup: " + e.getMessage());
        }

        Set<Tag> newTags = new HashSet<>(personToEdit.getTags());
        newTags.add(tag);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getStudyYear(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                newTags
        );

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);
        expectedModel.addSession(((SessionTag) tag).getSession());

        assertCommandSuccess(addSessionCommand, model,
                String.format(AddSessionCommand.MESSAGE_ADD_SESSION_SUCCESS,
                        Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_sameSessionDiffPersonFilteredList_success() {
        // edit person in filtered list to have overlap session tag in address book
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag tag = createSessionTag("MON", "1100", "1200");
        AddSessionCommand addSessionCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        AddSessionCommand addOtherSessionCommand = new AddSessionCommand(INDEX_SECOND_PERSON, tag);

        // The success path is already tested elsewhere; this execution only prepares the model for
        // the overlap-session check.
        try {
            addOtherSessionCommand.execute(model);
            showPersonAtIndex(model, INDEX_FIRST_PERSON);
        } catch (Exception e) {
            fail("Unexpected exception thrown during setup: " + e.getMessage());
        }

        Set<Tag> newTags = new HashSet<>(personToEdit.getTags());
        newTags.add(tag);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getStudyYear(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                newTags
        );

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);
        expectedModel.addSession(((SessionTag) tag).getSession());

        assertCommandSuccess(addSessionCommand, model,
                String.format(AddSessionCommand.MESSAGE_ADD_SESSION_SUCCESS,
                        Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Tag tag = createSessionTag("MON", "1100", "1200");
        AddSessionCommand addSessionCommand = new AddSessionCommand(outOfBoundIndex, tag);

        assertCommandFailure(addSessionCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        Tag tag = createSessionTag("MON", "1100", "1200");
        AddSessionCommand addSessionCommand = new AddSessionCommand(outOfBoundIndex, tag);

        assertCommandFailure(addSessionCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final Tag tag = createSessionTag("MON", "1100", "1200");
        final AddSessionCommand standardCommand = new AddSessionCommand(INDEX_FIRST_PERSON, tag);

        // same values -> returns true
        AddSessionCommand commandWithSameValues = new AddSessionCommand(INDEX_FIRST_PERSON, tag);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new AddSessionCommand(INDEX_SECOND_PERSON, tag)));

        // different session tag -> returns false
        assertFalse(standardCommand.equals(new AddSessionCommand(INDEX_FIRST_PERSON,
                createSessionTag("TUE", "1100", "1200"))));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        Tag tag = createSessionTag("MON", "1100", "1200");
        AddSessionCommand addSessionCommand = new AddSessionCommand(index, tag);
        String expected = AddSessionCommand.class.getCanonicalName() + "{targetIndex=" + index + ", toAdd="
                + tag + "}";
        assertEquals(expected, addSessionCommand.toString());
    }
}
