package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    private final MainWindow mainWindow;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList, MainWindow mainWindow) {
        super(FXML);
        this.mainWindow = mainWindow;
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        setupKeyboardNavigation();
    }

    /**
     * Sets up keyboard navigation for the person list.
     */
    private void setupKeyboardNavigation() {
        personListView.setOnKeyPressed(this::handleKeyPress);

        // Request focus when the panel is shown
        personListView.setFocusTraversable(true);
    }

    /**
     * Handles keyboard events for navigation and selection.
     */
    private void handleKeyPress(KeyEvent event) {
        KeyCode keyCode = event.getCode();

        // Handle Ctrl+Enter for selection
        if (keyCode == KeyCode.ENTER && event.isControlDown()) {
            Person selectedPerson = personListView.getSelectionModel().getSelectedItem();
            if (selectedPerson != null) {
                mainWindow.updateDetailedView(selectedPerson);
                event.consume(); // Prevent default behavior
            }
        } else if (keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
            // Let ListView handle the navigation first
            // Then update the detailed view after a short delay
            javafx.application.Platform.runLater(() -> {
                Person selectedPerson = personListView.getSelectionModel().getSelectedItem();
                if (selectedPerson != null) {
                    mainWindow.updateDetailedView(selectedPerson);
                }
            });
        } else if (isTypingKey(keyCode)) {
            mainWindow.focusCommandBox();
            // Don't consume the event - let it propagate to the command box
        }
    }

    /**
     * Checks if the key code represents a typing key (letters, numbers, space, etc.)
     */
    private boolean isTypingKey(KeyCode keyCode) {
        // Letters and digits
        if (keyCode.isLetterKey() || keyCode.isDigitKey()) {
            return true;
        }

        // Common typing keys
        switch (keyCode) {
        case SPACE:
        case SLASH:
        case MINUS:
        case PERIOD:
        case COMMA:
        case BACK_SPACE:
        case DELETE:
            return true;
        default:
            return false;
        }
    }

    /**
     * Requests focus on the person list view.
     */
    public void requestFocus() {
        personListView.requestFocus();
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1, mainWindow).getRoot());
            }
        }
    }

}
