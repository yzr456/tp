package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.Model;



public class HelpWindowTest {

    @Test
    public void constants_validValues() {
        assertEquals("https://ay2526s1-cs2103-f12-4.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
    }

    @Test
    public void getCommandUsage_containsExpectedCommands() {
        String commandUsage = HelpWindow.getCommandUsage();

        // Check that all expected commands are present
        assertTrue(commandUsage.contains("add"));
        assertTrue(commandUsage.contains("delete"));
        assertTrue(commandUsage.contains("list"));
        assertTrue(commandUsage.contains("find"));
        assertTrue(commandUsage.contains("help"));
        assertTrue(commandUsage.contains("exit"));
        assertTrue(commandUsage.contains("addsession"));
        assertTrue(commandUsage.contains("addsubject"));
        assertTrue(commandUsage.contains("free"));

        // Check that it contains usage information (should have multiple lines)
        assertTrue(commandUsage.contains("\n"));
        assertTrue(commandUsage.contains("▶"));
    }

    @Test
    public void getCommandUsage_withCustomCommands_returnsCorrectFormat() {
        List<Class<? extends Command>> testCommands = new ArrayList<>();
        testCommands.add(AddCommand.class);
        testCommands.add(ListCommand.class);

        String commandUsage = HelpWindow.getCommandUsage(testCommands);

        // Should contain only the specified commands
        assertTrue(commandUsage.contains("add"));
        assertTrue(commandUsage.contains("list"));
        assertFalse(commandUsage.contains("delete"));
        assertFalse(commandUsage.contains("find"));

        // Should still have proper formatting
        assertTrue(commandUsage.contains("▶"));
        assertTrue(commandUsage.contains("\n"));
    }


    @Test
    public void getCommandUsage_notEmpty() {
        String commandUsage = HelpWindow.getCommandUsage();
        assertFalse(commandUsage.isEmpty());
        assertTrue(commandUsage.length() > 0);
    }

    @Test
    public void getCommandUsage_invalidCommandInput() {
        List<Class<? extends Command>> invalidCommands = new ArrayList<>();
        invalidCommands.add(MockInvalidCommand.class);
        invalidCommands.add(MockInvalidMessageUsageCommand.class);
        String commandUsage = HelpWindow.getCommandUsage(invalidCommands);

        assertEquals("▶ Mock\n", commandUsage);

    }

    /**
     * Mock command class that lacks required static fields for testing
     */
    private static class MockInvalidCommand extends Command {
        @Override
        public CommandResult execute(Model model) {
            return null;
        }
    }

    /**
     * Mock command that lacks MESSAGE_USAGE static field
     */
    private static class MockInvalidMessageUsageCommand extends Command {
        public static final String COMMAND_WORD = "Mock";

        @Override
        public CommandResult execute(Model model) {
            return null;
        }

    }
}
