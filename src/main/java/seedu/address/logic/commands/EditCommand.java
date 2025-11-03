package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDY_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Session;
import seedu.address.model.person.StudyYear;
import seedu.address.model.tag.SessionTag;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Contact Edit Parameters: -c INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_STUDY_YEAR + "STUDY_YEAR] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_SUBJECT + "SUBJECT]...\n"
            + "Session Edit Parameters: -s INDEX (must be a positive integer) "
            + "[" + PREFIX_DAY + "DAY] "
            + "[" + PREFIX_START + "START] "
            + "[" + PREFIX_END + "END]...(START and END must be between 0800 to 2200)\n"
            + "Example (Contact): " + COMMAND_WORD + " -c 1 "
            + PREFIX_NAME + "John Doe "
            + PREFIX_STUDY_YEAR + "SEC3 "
            + PREFIX_PHONE + "99999999 "
            + PREFIX_EMAIL + "johndoe@example.com "
            + PREFIX_ADDRESS + "21 Lower Kent Ridge Road\n"
            + "Example (Session): " + COMMAND_WORD + " -s 1 "
            + PREFIX_DAY + "MON "
            + PREFIX_START + "0900 "
            + PREFIX_END + "1100\n";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "No fields specified for editing. "
            + "Please provide at least one field to edit (e.g., n/NAME, p/PHONE, e/EMAIL).";
    public static final String MESSAGE_DUPLICATE_EMAIL = "A person with this email address "
            + "already exists in the address book.";
    public static final String MESSAGE_DUPLICATE_PHONE_AND_EMAIL = "A person with this phone number "
            + "and email address already exists in the address book.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_DUPLICATE_PHONE = "A person with this phone number "
            + "already exists in the address book.";
    public static final String MESSAGE_INVALID_FLAG = "Invalid flag provided. "
            + "Use -c for contact or -s for session.";
    public static final String MESSAGE_MISSING_FLAG = "A valid flag must be provided. "
            + "Use -c for contact or -s for session.";
    public static final String MESSAGE_MISSING_ARGUMENTS = "Missing arguments after flag. "
            + "Format: edit -c INDEX [fields...] or edit -s INDEX [fields...]";
    public static final String MESSAGE_INVALID_SESSION_SEQUENCE =
            "Invalid session format. Each session must follow order: d/, s/, e/. \n"
                    + "Example: d/MON s/1100 e/1200 d/TUE s/1300 e/1400";
    public static final String MESSAGE_OVERLAPPING_SESSION = "The sessions overlap with these: %s session(s).\n";
    public static final String MESSAGE_CONFLICTING_SUBJECT_OPERATION =
            "Cannot clear and add subjects in the same command. Use 'sub/' alone to clear all subjects.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    /**
     * Executes the edit command to modify a person's details in the address book.
     * This method handles session management, validates for duplicates and overlapping sessions,
     * and updates the person in the model.
     *
     * @param model The model which the command should operate on.
     * @return The result of the command execution.
     * @throws CommandException If the person index is invalid, duplicate person exists,
     *                          or sessions overlap.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToEdit = getPersonToEdit(model);
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        validateNoDuplicatePerson(model, personToEdit, editedPerson);

        List<Session> removedSessions = removeOldSessions(model, personToEdit);

        try {
            List<Session> newSessions = extractSessions(editedPerson);
            validateNoOverlappingSessions(newSessions, model);
            addNewSessions(model, newSessions);
        } catch (CommandException e) {
            reinstateSessions(model, removedSessions);
            throw e;
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Retrieves the person to edit from the filtered person list.
     *
     * @param model The model containing the person list.
     * @return The person at the specified index.
     * @throws CommandException If the index is out of bounds.
     */
    private Person getPersonToEdit(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownList.get(index.getZeroBased());
    }

    /**
     * Validates that the edited person does not create a duplicate in the address book.
     *
     * @param model The model to check for duplicates.
     * @param personToEdit The original person being edited.
     * @param editedPerson The person with edited details.
     * @throws CommandException If a duplicate person or contact exists.
     */
    private void validateNoDuplicatePerson(Model model, Person personToEdit, Person editedPerson)
            throws CommandException {
        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
        if (model.hasContactExcluding(editedPerson, personToEdit)) {
            // Find which field(s) are duplicated
            String errorMessage = getDuplicateContactMessage(model, editedPerson, personToEdit);
            throw new CommandException(errorMessage);
        }
    }

    /**
     * Determines which contact field (phone, email, or both) is duplicated in the model,
     * excluding the person being edited.
     */
    private String getDuplicateContactMessage(Model model, Person editedPerson, Person personToEdit) {
        boolean hasPhoneDuplicate = false;
        boolean hasEmailDuplicate = false;

        for (Person person : model.getAddressBook().getPersonList()) {
            // Skip the person being edited
            if (person.equals(personToEdit)) {
                continue;
            }

            if (person.hasSameNumber(editedPerson)) {
                hasPhoneDuplicate = true;
            }
            if (person.hasSameEmail(editedPerson)) {
                hasEmailDuplicate = true;
            }
            if (hasPhoneDuplicate && hasEmailDuplicate) {
                break; // Both found, no need to continue
            }
        }

        if (hasPhoneDuplicate && hasEmailDuplicate) {
            return MESSAGE_DUPLICATE_PHONE_AND_EMAIL;
        } else if (hasPhoneDuplicate) {
            return MESSAGE_DUPLICATE_PHONE;
        } else {
            return MESSAGE_DUPLICATE_EMAIL;
        }
    }

    /**
     * Removes all sessions associated with the person from the model.
     * This is done before applying the edits to ensure clean session management.
     *
     * @param model The model from which to remove sessions.
     * @param person The person whose sessions should be removed.
     * @return A list of removed sessions for potential reinstatement if validation fails.
     */
    private List<Session> removeOldSessions(Model model, Person person) {
        List<Session> removedSessions = new ArrayList<>();
        for (Tag tag : person.getTags()) {
            if (tag.isSessionTag()) {
                SessionTag sessionTag = (SessionTag) tag;
                Session session = sessionTag.getSession();
                model.removeSession(session);
                removedSessions.add(session);
            }
        }
        return removedSessions;
    }

    /**
     * Extracts all sessions from a person's tags.
     *
     * @param person The person from whom to extract sessions.
     * @return A list of all sessions associated with the person.
     */
    private List<Session> extractSessions(Person person) {
        List<Session> sessions = new ArrayList<>();
        for (Tag tag : person.getTags()) {
            if (tag.isSessionTag()) {
                sessions.add(((SessionTag) tag).getSession());
            }
        }
        return sessions;
    }

    /**
     * Validates that none of the new sessions overlap with each other or with existing sessions.
     *
     * @param newSessions The list of new sessions to validate.
     * @param model The model to check against existing sessions.
     * @throws CommandException If any overlapping sessions are detected.
     */
    private void validateNoOverlappingSessions(List<Session> newSessions, Model model)
            throws CommandException {
        checkInternalOverlaps(newSessions);
        checkExternalOverlaps(newSessions, model);
    }

    /**
     * Checks for overlaps within the list of new sessions.
     *
     * @param sessions The list of sessions to check for internal overlaps.
     * @throws CommandException If any two sessions in the list overlap.
     */
    private void checkInternalOverlaps(List<Session> sessions) throws CommandException {
        for (int i = 0; i < sessions.size(); i++) {
            for (int j = i + 1; j < sessions.size(); j++) {
                if (sessions.get(i).isOverlap(sessions.get(j))) {
                    throw new CommandException(
                            String.format(MESSAGE_OVERLAPPING_SESSION, sessions.get(j)));
                }
            }
        }
    }

    /**
     * Checks for overlaps between new sessions and existing sessions in the model.
     *
     * @param newSessions The list of new sessions to validate.
     * @param model The model containing existing sessions.
     * @throws CommandException If any new session overlaps with an existing session
     *                          (unless they are exact duplicates).
     */
    private void checkExternalOverlaps(List<Session> newSessions, Model model)
            throws CommandException {
        for (Session session : newSessions) {
            Optional<Session> overlapping = model.getOverlappingSession(session);
            if (overlapping.isPresent() && !session.equals(overlapping.get())) {
                throw new CommandException(
                        String.format(MESSAGE_OVERLAPPING_SESSION, overlapping.get()));
            }
        }
    }

    /**
     * Adds all validated sessions to the model.
     *
     * @param model The model to which sessions should be added.
     * @param sessions The list of sessions to add.
     */
    private void addNewSessions(Model model, List<Session> sessions) {
        sessions.forEach(model::addSession);
    }

    /**
     * Reinstates previously removed sessions to the model.
     * This is called when validation fails to restore the model to its previous state.
     *
     * @param model The model to which sessions should be reinstated.
     * @param removedSessions The list of sessions to reinstate.
     */
    private void reinstateSessions(Model model, List<Session> removedSessions) {
        for (Session session : removedSessions) {
            model.addSession(session);
        }
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        StudyYear updatedStudyYear = editPersonDescriptor.getStudyYear().orElse(personToEdit.getStudyYear());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());

        // Handle tags - need to separate subjects and sessions
        Set<Tag> updatedTags = new HashSet<>();

        // Separate existing tags into subjects and sessions
        Set<Tag> existingSubjects = new HashSet<>();
        Set<Tag> existingSessions = new HashSet<>();
        for (Tag tag : personToEdit.getTags()) {
            if (isSessionTag(tag)) {
                existingSessions.add(tag);
            } else {
                existingSubjects.add(tag);
            }
        }

        // Handle subjects - replace if provided, otherwise keep existing
        if (editPersonDescriptor.getSubjects().isPresent()) {
            updatedTags.addAll(editPersonDescriptor.getSubjects().get());
        } else {
            updatedTags.addAll(existingSubjects);
        }

        // Handle sessions - replace if provided, otherwise keep existing
        if (editPersonDescriptor.getSessions().isPresent()) {
            updatedTags.addAll(editPersonDescriptor.getSessions().get());
        } else {
            updatedTags.addAll(existingSessions);
        }

        return new Person(updatedName, updatedStudyYear, updatedPhone, updatedEmail, updatedAddress, updatedTags);
    }

    /**
     * Returns true if the tag is a session tag (contains spaces, indicating day and time format).
     */
    private static boolean isSessionTag(Tag tag) {
        return tag.tagName.contains(" ");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private StudyYear studyYear;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> subjects;
        private Set<Tag> sessions;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setStudyYear(toCopy.studyYear);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setSubjects(toCopy.subjects);
            setSessions(toCopy.sessions);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, studyYear, phone, email, address, subjects, sessions);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setStudyYear(StudyYear studyYear) {
            this.studyYear = studyYear;
        }

        public Optional<StudyYear> getStudyYear() {
            return Optional.ofNullable(studyYear);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code subjects} to this object's {@code subjects}.
         * A defensive copy of {@code subjects} is used internally.
         */
        public void setSubjects(Set<Tag> subjects) {
            this.subjects = (subjects != null) ? new HashSet<>(subjects) : null;
        }

        /**
         * Returns an unmodifiable subject set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code subjects} is null.
         */
        public Optional<Set<Tag>> getSubjects() {
            return (subjects != null) ? Optional.of(Collections.unmodifiableSet(subjects)) : Optional.empty();
        }

        /**
         * Sets {@code sessions} to this object's {@code sessions}.
         * A defensive copy of {@code sessions} is used internally.
         */
        public void setSessions(Set<Tag> sessions) {
            this.sessions = (sessions != null) ? new HashSet<>(sessions) : null;
        }

        /**
         * Returns an unmodifiable session set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code sessions} is null.
         */
        public Optional<Set<Tag>> getSessions() {
            return (sessions != null) ? Optional.of(Collections.unmodifiableSet(sessions)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(studyYear, otherEditPersonDescriptor.studyYear)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(subjects, otherEditPersonDescriptor.subjects)
                    && Objects.equals(sessions, otherEditPersonDescriptor.sessions);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("studyYear", studyYear)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("subjects", subjects)
                    .add("sessions", sessions)
                    .toString();
        }
    }
}
