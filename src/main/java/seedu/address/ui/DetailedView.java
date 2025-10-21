package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/**
 * Panel containing the detailed view of a selected person.
 */
public class DetailedView extends UiPart<Region> {

    private static final String FXML = "DetailedView.fxml";

    @FXML
    private Label contactName;
    @FXML
    private Label nameLabel;
    @FXML
    private Label studyYearLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private FlowPane subjectTags;
    @FXML
    private VBox sessionsList;

    /**
     * Creates an empty detailed view panel with no contact selected.
     */
    public DetailedView() {
        super(FXML);
        showEmptyState();
    }

    /**
     * Displays the details of the given person.
     */
    public void setPerson(Person person) {
        if (person == null) {
            showEmptyState();
            return;
        }

        // Update header with person's name in large format
        contactName.setText(person.getName().fullName.toUpperCase());

        // Update all detail fields
        nameLabel.setText(person.getName().fullName);
        studyYearLabel.setText(person.getStudyYear().value);
        phoneLabel.setText(person.getPhone().value);
        emailLabel.setText(person.getEmail().value);
        addressLabel.setText(person.getAddress().value);

        // Clear previous tags
        subjectTags.getChildren().clear();
        sessionsList.getChildren().clear();

        // Add subject tags
        person.getTags().forEach(tag -> {
            Label tagLabel = new Label(tag.tagName);
            tagLabel.getStyleClass().add("tag");
            // Add color-specific style class based on tag name
            tagLabel.getStyleClass().add("tag-" + tag.tagName.toLowerCase());
            subjectTags.getChildren().add(tagLabel);
        });

        // Add sessions placeholder
        // TODO: Implement when Session class is ready
        Label sessionPlaceholder = new Label("No sessions added yet");
        sessionPlaceholder.getStyleClass().add("session-placeholder");
        sessionsList.getChildren().add(sessionPlaceholder);
    }

    /**
     * Shows empty state when no contact is selected.
     */
    private void showEmptyState() {
        contactName.setText("Select a contact");
        nameLabel.setText("-");
        studyYearLabel.setText("-");
        phoneLabel.setText("-");
        emailLabel.setText("-");
        addressLabel.setText("-");
        subjectTags.getChildren().clear();
        sessionsList.getChildren().clear();
    }
}
