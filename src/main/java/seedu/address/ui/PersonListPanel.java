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
        }
        // Typing key handling is now done globally in MainWindow
    }

    /**
     * Requests focus on the person list view.
     */
    public void requestFocus() {
        personListView.requestFocus();
    }

    /**
     * Requests focus on the person list view and selects the first item.
     */
    public void requestFocusAndSelectFirst() {
        personListView.requestFocus();
        if (!personListView.getItems().isEmpty()) {
            personListView.getSelectionModel().selectFirst();
            javafx.application.Platform.runLater(() -> {
                Person selectedPerson = personListView.getSelectionModel().getSelectedItem();
                if (selectedPerson != null) {
                    mainWindow.updateDetailedView(selectedPerson);
                }
            });
        }
    }

    /**
     * Checks if the person list view has focus.
     */
    public boolean isFocused() {
        return personListView.isFocused();
    }

    /**
     * Checks if a person is currently selected.
     */
    public boolean hasSelection() {
        return personListView.getSelectionModel().getSelectedItem() != null;
    }

    /**
     * Returns the currently selected person, or null if none is selected.
     */
    public Person getSelectedPerson() {
        return personListView.getSelectionModel().getSelectedItem();
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
