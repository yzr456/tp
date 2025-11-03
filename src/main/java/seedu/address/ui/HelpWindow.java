package seedu.address.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.commands.AddSubjectCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FreeCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SetPaymentCommand;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s1-cs2103-f12-4.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE_PREFIX = "Refer to the user guide: ";
    public static final String HELP_MESSAGE = HELP_MESSAGE_PREFIX + USERGUIDE_URL;

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private Label commandUsageMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        commandUsageMessage.setText(getCommandUsage());
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        if (getRoot().isIconified()) {
            getRoot().setIconified(false);
        }
        getRoot().requestFocus();
        getRoot().toFront();
    }

    /**
     * @return A string containing how to use the commands
     */
    public static String getCommandUsage() {
        //mini command registry
        List<Class<? extends Command>> registeredCommands = new ArrayList<>();
        registeredCommands.add(AddCommand.class);
        registeredCommands.add(AddSessionCommand.class);
        registeredCommands.add(AddSubjectCommand.class);
        registeredCommands.add(EditCommand.class);
        registeredCommands.add(DeleteCommand.class);
        registeredCommands.add(ListCommand.class);
        registeredCommands.add(FindCommand.class);
        registeredCommands.add(FreeCommand.class);
        registeredCommands.add(HelpCommand.class);
        registeredCommands.add(ExitCommand.class);
        registeredCommands.add(SetPaymentCommand.class);

        return getCommandUsage(registeredCommands);
    }

    /**
     * @param commands The list of command classes to generate usage for
     * @return A string containing how to use the commands
     */
    public static String getCommandUsage(List<Class<? extends Command>> commands) {
        StringBuilder commandUsages = new StringBuilder();

        for (Class<? extends Command> command : commands) {
            try {
                Field commandWord = command.getDeclaredField("COMMAND_WORD");
                commandUsages.append("▶ ").append(commandWord.get(null)).append("\n");
                Field messageUsage = command.getDeclaredField("MESSAGE_USAGE");
                commandUsages.append(messageUsage.get(null));
                commandUsages.append("\n");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.warning(e.getMessage());
            }
        }
        return commandUsages.toString();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
