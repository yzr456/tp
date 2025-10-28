package seedu.address.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

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
    private Label paymentStatusLabel;
    @FXML
    private Label billingStartLabel;
    @FXML
    private FlowPane subjectTags;
    @FXML
    private FlowPane sessionsList;

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
        paymentStatusLabel.setText(person.getPayment().toString());
        billingStartLabel.setText("Day " + person.getPayment().getBillingStartDay() + " of each month");

        // Clear previous tags
        subjectTags.getChildren().clear();
        sessionsList.getChildren().clear();

        // Separate subject tags and session tags
        AtomicBoolean hasSessionTags = new AtomicBoolean(false);

        person.getTags().forEach(tag -> {
            if (tag.isSessionTag()) {
                // This is a session tag - add to sessions list
                hasSessionTags.set(true);
            } else {
                // This is a subject tag - add to subject tags
                Label tagLabel = new Label(tag.tagName);
                tagLabel.getStyleClass().add("tag");
                // Add color-specific style class based on tag name
                tagLabel.getStyleClass().add("tag-" + tag.tagName.toLowerCase());
                subjectTags.getChildren().add(tagLabel);
            }
        });

        // Add session tags to sessions list
        person.getTags().stream()
                .filter(Tag::isSessionTag)
                .forEach(tag -> {
                    Label sessionLabel = new Label(tag.tagName);
                    sessionLabel.getStyleClass().add("session-tag");

                    // Extract day from tag name (e.g., "MON 0900 - 1200" -> "MON")
                    String tagText = tag.tagName.toUpperCase();
                    if (tagText.startsWith("MON")) {
                        sessionLabel.getStyleClass().add("session-mon");
                    } else if (tagText.startsWith("TUE")) {
                        sessionLabel.getStyleClass().add("session-tue");
                    } else if (tagText.startsWith("WED")) {
                        sessionLabel.getStyleClass().add("session-wed");
                    } else if (tagText.startsWith("THU")) {
                        sessionLabel.getStyleClass().add("session-thu");
                    } else if (tagText.startsWith("FRI")) {
                        sessionLabel.getStyleClass().add("session-fri");
                    } else if (tagText.startsWith("SAT")) {
                        sessionLabel.getStyleClass().add("session-sat");
                    } else if (tagText.startsWith("SUN")) {
                        sessionLabel.getStyleClass().add("session-sun");
                    }

                    sessionsList.getChildren().add(sessionLabel);
                });

        // Add placeholder if no sessions
        if (!hasSessionTags.get()) {
            Label sessionPlaceholder = new Label("No sessions added yet");
            sessionPlaceholder.getStyleClass().add("session-placeholder");
            sessionsList.getChildren().add(sessionPlaceholder);
        }
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
        paymentStatusLabel.setText("-");
        billingStartLabel.setText("-");
        subjectTags.getChildren().clear();
        sessionsList.getChildren().clear();
    }
}
