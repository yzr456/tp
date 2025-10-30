package seedu.address.ui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private ResultDisplay resultDisplay;
    private CommandBox commandBox;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;


    @FXML
    private javafx.scene.control.Button helpButton;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private StackPane detailedViewPlaceholder;

    @FXML
    private javafx.scene.control.SplitPane splitPane;

    private DetailedView detailedView;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAcceleratorForButton(helpButton, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator for a Button.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAcceleratorForButton(javafx.scene.control.Button button, KeyCombination keyCombination) {
        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (keyCombination.match(event)) {
                button.fire();
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList(), this);
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        detailedView = new DetailedView();
        detailedViewPlaceholder.getChildren().add(detailedView.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        // Set up global keyboard handlers for the window
        setupGlobalKeyHandlers();

        // Give initial focus to person list for keyboard navigation
        Platform.runLater(() -> {
            personListPanel.requestFocus();
        });
    }

    /**
     * Sets up global keyboard shortcuts for the entire window.
     */
    private void setupGlobalKeyHandlers() {
        // Add event filter to catch keys at window level
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();

            // Handle UP/DOWN arrows from anywhere
            if (keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
                focusPersonListSmart(keyCode);
                // Don't consume if already in person list - let it navigate
                if (!personListPanel.isFocused()) {
                    event.consume();
                }
                return;
            }

            // If command box already has focus, do nothing for typing keys
            if (commandBox.isFocused()) {
                return;
            }

            // Don't intercept other navigation keys or Enter
            if (isNavigationKey(keyCode) || keyCode == KeyCode.ENTER) {
                return;
            }

            // For typing keys, focus command box and insert the character
            if (isTypingKey(keyCode)) {
                // IMPORTANT: Consume event first to prevent duplicate input
                event.consume();
                // Then handle the key insertion asynchronously
                Platform.runLater(() -> focusCommandBoxWithKey(event));
            }
        });
    }

    /**
     * Checks if the key is a navigation key.
     */
    private boolean isNavigationKey(KeyCode keyCode) {
        switch (keyCode) {
        case UP:
        case DOWN:
        case HOME:
        case END:
        case PAGE_UP:
        case PAGE_DOWN:
        case LEFT:
        case RIGHT:
        case TAB:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks if the key is a typing key.
     */
    private boolean isTypingKey(KeyCode keyCode) {
        return keyCode.isLetterKey()
                || keyCode.isDigitKey()
                || keyCode == KeyCode.SPACE
                || keyCode == KeyCode.SLASH
                || keyCode == KeyCode.MINUS
                || keyCode == KeyCode.BACK_SPACE
                || keyCode == KeyCode.DELETE
                || keyCode == KeyCode.CONTROL;
    }

    /**
     * Focuses on the command box for user input.
     */
    public void focusCommandBox() {
        if (commandBox != null) {
            commandBox.requestFocus();
        }
    }

    /**
     * Focuses on the command box and inserts the character from the key event.
     */
    public void focusCommandBoxWithKey(KeyEvent event) {
        if (commandBox != null) {
            commandBox.requestFocus();
            // Insert the character that was typed
            String text = event.getText();
            if (text != null && !text.isEmpty()) {
                commandBox.appendText(text);
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                commandBox.handleBackspace();
            } else if (event.getCode() == KeyCode.DELETE) {
                commandBox.handleDelete();
            }
        }
    }

    /**
     * Focuses on the person list panel and selects the first item.
     */
    public void focusPersonList() {
        if (personListPanel != null) {
            personListPanel.requestFocusAndSelectFirst();
        }
    }

    /**
     * Smart navigation for UP/DOWN arrows.
     * If a person is already selected, just focus the list (allowing relative navigation).
     * If no person is selected, focus and select the first person.
     */
    public void focusPersonListSmart(KeyCode keyCode) {
        if (personListPanel != null) {
            if (personListPanel.hasSelection()) {
                // There's already a selection, just focus and let normal navigation work
                personListPanel.requestFocus();
            } else {
                // No selection, select first item
                personListPanel.requestFocusAndSelectFirst();
            }
        }
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
        Platform.runLater(() -> {
            splitPane.setDividerPositions(0.30);
        });

        // Lock the divider at 30% position - prevents reset on window resize
        if (!splitPane.getDividers().isEmpty()) {
            splitPane.getDividers().get(0).positionProperty().addListener((observable,
                    oldValue, newValue) -> {
                        // Only reset if it drifts too far from 0.30
                        if (Math.abs(newValue.doubleValue() - 0.30) > 0.01) {
                            splitPane.setDividerPositions(0.30);
                        }
                    }
            );
        }
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }
            // Return focus to person list for fast typing
            Platform.runLater(() -> {
                personListPanel.requestFocus();
            });
            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            // Return focus to person list even on error
            Platform.runLater(() -> {
                personListPanel.requestFocus();
            });
            throw e;
        }
    }

    /**
     * Updates the detailed view with the selected person's information.
     */
    public void updateDetailedView(Person person) {
        detailedView.setPerson(person);
    }
}
