package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Session;
import seedu.address.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for FreeCommand.
 */
public class FreeCommandTest {

    private Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_validDuration_success() {
        FreeCommand freeCommand = new FreeCommand(1);
        assertEquals(1, freeCommand.specifiedDuration);
    }

    @Test
    public void execute_validDuration_success() throws Exception {
        FreeCommand freeCommand = new FreeCommand(1);
        CommandResult result = freeCommand.execute(model);

        // Verify that the result contains expected format
        String resultMessage = result.getFeedbackToUser();
        assertTrue(resultMessage.contains("hour time slot is at:"));
    }

    @Test
    public void execute_withExistingSessions_findsCorrectFreeTime() throws Exception {
        // Add a session to the model
        Session session = new Session("MON", "0900", "1000");
        model.addSession(session);

        FreeCommand freeCommand = new FreeCommand(1);
        CommandResult result = freeCommand.execute(model);

        String resultMessage = result.getFeedbackToUser();
        // Should find time after the existing session or at a different time
        assertTrue(resultMessage.contains("hour time slot is at:"));
    }

    @Test
    public void execute_largeDuration_success() throws Exception {
        FreeCommand freeCommand = new FreeCommand(5);
        CommandResult result = freeCommand.execute(model);

        String resultMessage = result.getFeedbackToUser();
        assertTrue(resultMessage.contains("5 hour time slot is at:"));
    }

    @Test
    public void execute_invalidDuration_failure() throws Exception {
        FreeCommand freeCommand = new FreeCommand(15);
        assertThrows(CommandException.class, () -> freeCommand.execute(model));
    }

    @Test
    public void execute_zeroDuration_failure() throws Exception {
        FreeCommand freeCommand = new FreeCommand(0);
        assertThrows(CommandException.class, () -> freeCommand.execute(model));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        FreeCommand freeCommand = new FreeCommand(1);
        assertThrows(NullPointerException.class, () -> freeCommand.execute(null));
    }

    @Test
    public void equals() {
        FreeCommand freeCommand1 = new FreeCommand(1);
        FreeCommand freeCommand2 = new FreeCommand(1);
        FreeCommand freeCommand3 = new FreeCommand(2);

        // same object -> returns true
        assertTrue(freeCommand1.equals(freeCommand1));

        // same values -> returns true
        assertTrue(freeCommand1.equals(freeCommand2));

        // different types -> returns false
        assertFalse(freeCommand1.equals(1));

        // null -> returns false
        assertFalse(freeCommand1.equals(null));

        // different duration -> returns false
        assertFalse(freeCommand1.equals(freeCommand3));
    }

    @Test
    public void toStringMethod() {
        FreeCommand freeCommand = new FreeCommand(2);
        String expected = FreeCommand.class.getCanonicalName() + "{specifiedDuration=2}";
        assertEquals(expected, freeCommand.toString());
    }
}
