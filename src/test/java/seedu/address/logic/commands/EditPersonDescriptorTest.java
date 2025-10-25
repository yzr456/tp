package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDY_YEAR_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Session;
import seedu.address.model.tag.SessionTag;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different study year -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withStudyYear(VALID_STUDY_YEAR_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", studyYear="
                + editPersonDescriptor.getStudyYear().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", address="
                + editPersonDescriptor.getAddress().orElse(null) + ", subjects="
                + editPersonDescriptor.getSubjects().orElse(null) + ", sessions="
                + editPersonDescriptor.getSessions().orElse(null) + "}";
        assertEquals(expected, editPersonDescriptor.toString());
    }

    @Test
    public void setSubjects_validSubjects_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("MATH"));
        subjects.add(new Tag("SCI"));

        descriptor.setSubjects(subjects);

        assertTrue(descriptor.getSubjects().isPresent());
        assertEquals(subjects, descriptor.getSubjects().get());
    }

    @Test
    public void setSessions_validSessions_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> sessions = new HashSet<>();
        Session session1 = new Session("MON", "1000", "1100");
        Session session2 = new Session("WED", "1400", "1500");
        sessions.add(new SessionTag(session1.toString(), session1));
        sessions.add(new SessionTag(session2.toString(), session2));

        descriptor.setSessions(sessions);

        assertTrue(descriptor.getSessions().isPresent());
        assertEquals(sessions, descriptor.getSessions().get());
    }

    @Test
    public void setSubjects_emptySet_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> emptySubjects = new HashSet<>();

        descriptor.setSubjects(emptySubjects);

        assertTrue(descriptor.getSubjects().isPresent());
        assertEquals(emptySubjects, descriptor.getSubjects().get());
    }

    @Test
    public void setSessions_emptySet_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> emptySessions = new HashSet<>();

        descriptor.setSessions(emptySessions);

        assertTrue(descriptor.getSessions().isPresent());
        assertEquals(emptySessions, descriptor.getSessions().get());
    }

    @Test
    public void copyConstructor_withSubjects_success() {
        EditPersonDescriptor original = new EditPersonDescriptor();
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("MATH"));
        original.setSubjects(subjects);

        EditPersonDescriptor copy = new EditPersonDescriptor(original);

        assertTrue(copy.getSubjects().isPresent());
        assertEquals(original.getSubjects().get(), copy.getSubjects().get());
    }

    @Test
    public void copyConstructor_withSessions_success() {
        EditPersonDescriptor original = new EditPersonDescriptor();
        Set<Tag> sessions = new HashSet<>();
        Session session = new Session("TUE", "0900", "1000");
        sessions.add(new SessionTag(session.toString(), session));
        original.setSessions(sessions);

        EditPersonDescriptor copy = new EditPersonDescriptor(original);

        assertTrue(copy.getSessions().isPresent());
        assertEquals(original.getSessions().get(), copy.getSessions().get());
    }

    @Test
    public void isAnyFieldEdited_withSubjectsOnly_returnsTrue() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("ENGLISH"));
        descriptor.setSubjects(subjects);

        assertTrue(descriptor.isAnyFieldEdited());
    }

    @Test
    public void isAnyFieldEdited_withSessionsOnly_returnsTrue() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> sessions = new HashSet<>();
        Session session = new Session("FRI", "1600", "1700");
        sessions.add(new SessionTag(session.toString(), session));
        descriptor.setSessions(sessions);

        assertTrue(descriptor.isAnyFieldEdited());
    }

    @Test
    public void isAnyFieldEdited_withBothSubjectsAndSessions_returnsTrue() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("PHYSICS"));
        Set<Tag> sessions = new HashSet<>();
        Session session = new Session("THU", "1300", "1400");
        sessions.add(new SessionTag(session.toString(), session));

        descriptor.setSubjects(subjects);
        descriptor.setSessions(sessions);

        assertTrue(descriptor.isAnyFieldEdited());
    }

    @Test
    public void equals_differentSubjects_returnsFalse() {
        EditPersonDescriptor descriptor1 = new EditPersonDescriptor();
        Set<Tag> subjects1 = new HashSet<>();
        subjects1.add(new Tag("MATH"));
        descriptor1.setSubjects(subjects1);

        EditPersonDescriptor descriptor2 = new EditPersonDescriptor();
        Set<Tag> subjects2 = new HashSet<>();
        subjects2.add(new Tag("SCI"));
        descriptor2.setSubjects(subjects2);

        assertFalse(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_differentSessions_returnsFalse() {
        EditPersonDescriptor descriptor1 = new EditPersonDescriptor();
        Set<Tag> sessions1 = new HashSet<>();
        Session session1 = new Session("MON", "1000", "1100");
        sessions1.add(new SessionTag(session1.toString(), session1));
        descriptor1.setSessions(sessions1);

        EditPersonDescriptor descriptor2 = new EditPersonDescriptor();
        Set<Tag> sessions2 = new HashSet<>();
        Session session2 = new Session("TUE", "1000", "1100");
        sessions2.add(new SessionTag(session2.toString(), session2));
        descriptor2.setSessions(sessions2);

        assertFalse(descriptor1.equals(descriptor2));
    }

    @Test
    public void equals_sameSubjectsAndSessions_returnsTrue() {
        EditPersonDescriptor descriptor1 = new EditPersonDescriptor();
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("MATH"));
        Set<Tag> sessions = new HashSet<>();
        Session session = new Session("WED", "1400", "1500");
        sessions.add(new SessionTag(session.toString(), session));
        descriptor1.setSubjects(subjects);
        descriptor1.setSessions(sessions);

        EditPersonDescriptor descriptor2 = new EditPersonDescriptor();
        descriptor2.setSubjects(subjects);
        descriptor2.setSessions(sessions);

        assertTrue(descriptor1.equals(descriptor2));
    }
}
