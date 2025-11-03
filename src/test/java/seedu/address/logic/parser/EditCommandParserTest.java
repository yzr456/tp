package seedu.address.logic.parser;

import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DAY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_END_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_START_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_STUDY_YEAR_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.STUDY_YEAR_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.STUDY_YEAR_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DAY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_END_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_START_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDY_YEAR_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDY_YEAR_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDY_YEAR;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddSubjectCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Session;
import seedu.address.model.person.StudyYear;
import seedu.address.model.tag.SessionTag;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no flag specified
        assertParseFailure(parser, VALID_NAME_AMY, EditCommand.MESSAGE_MISSING_FLAG);

        // no index specified - valid flag with no arguments
        assertParseFailure(parser, "-c", EditCommand.MESSAGE_MISSING_ARGUMENTS);

        // no index specified - valid flag with name but no index
        assertParseFailure(parser, "-c " + NAME_DESC_AMY,
                String.format(Messages.MESSAGE_MISSING_INDEX, EditCommand.MESSAGE_USAGE));

        // no field specified
        assertParseFailure(parser, "-c 1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-c -5" + NAME_DESC_AMY, MESSAGE_INVALID_INDEX);

        // zero index
        assertParseFailure(parser, "-c 0" + NAME_DESC_AMY, MESSAGE_INVALID_INDEX);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "-c 1 some random string", EditCommand.MESSAGE_NOT_EDITED);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "-c 1 i/ string", EditCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "-c 1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "-c 1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "-c 1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "-c 1" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address

        // invalid phone followed by valid email
        assertParseFailure(parser, "-c 1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "-c 1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY
                + VALID_PHONE_AMY, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + PHONE_DESC_BOB
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = "-c " + targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = "-c " + targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = "-c " + targetIndex.getOneBased() + ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = "-c " + targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = "-c " + targetIndex.getOneBased() + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = "-c " + targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_invalidFlag_failure() {
        // invalid flag -x
        assertParseFailure(parser, "-x 1" + NAME_DESC_AMY, EditCommand.MESSAGE_INVALID_FLAG);

        // invalid flag -a
        assertParseFailure(parser, "-a 1" + PHONE_DESC_AMY, EditCommand.MESSAGE_INVALID_FLAG);

        // invalid flag -session
        assertParseFailure(parser, "-session 1" + NAME_DESC_AMY, EditCommand.MESSAGE_INVALID_FLAG);
    }

    @Test
    public void parse_validContactFlag_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_validSessionFlag_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-s " + targetIndex.getOneBased() + VALID_DAY_DESC + VALID_START_DESC + VALID_END_DESC;

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> sessions = new HashSet<>();
        Session session = new Session("MON", "1100", "1200");
        sessions.add(new SessionTag(session.toString(), session));
        descriptor.setSessions(sessions);

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_sessionEditAllFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-s " + targetIndex.getOneBased() + VALID_DAY_DESC + VALID_START_DESC + VALID_END_DESC;

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> sessions = new HashSet<>();
        Session session = new Session("MON", "1100", "1200");
        sessions.add(new SessionTag(session.toString(), session));
        descriptor.setSessions(sessions);

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_sessionEditMultipleSessions_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-s " + targetIndex.getOneBased()
                + " d/MON s/1100 e/1200 d/WED s/1400 e/1500";

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> sessions = new HashSet<>();
        Session session1 = new Session("MON", "1100", "1200");
        Session session2 = new Session("WED", "1400", "1500");
        sessions.add(new SessionTag(session1.toString(), session1));
        sessions.add(new SessionTag(session2.toString(), session2));
        descriptor.setSessions(sessions);

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_sessionEditMismatchedParameters_failure() {
        // expected messages for different invalid patterns
        String expectedMessage = EditCommand.MESSAGE_INVALID_SESSION_SEQUENCE;
        String expectedMessage2 = String.format(Messages.MESSAGE_MISSING_PREFIX, EditCommand.MESSAGE_USAGE);

        // Out of order prefixes — multiple days before starts
        String userInput = "-s 1 d/MON d/TUE s/1000 e/1100";
        assertParseFailure(parser, userInput, expectedMessage);

        // Starts repeated before end — also invalid sequence
        userInput = "-s 1 d/MON s/1000 s/1100 e/1100";
        assertParseFailure(parser, userInput, expectedMessage);

        // Ends repeated before next day — invalid sequence again
        userInput = "-s 1 d/MON s/1000 e/1100 e/1200";
        assertParseFailure(parser, userInput, expectedMessage);

        // Missing start → incomplete triplet
        userInput = "-s 1 d/MON e/1100";
        assertParseFailure(parser, userInput, expectedMessage2);

        // Missing end → incomplete triplet
        userInput = "-s 1 d/MON s/1000";
        assertParseFailure(parser, userInput, expectedMessage2);

        // Missing day → invalid sequence (cannot start with s/)
        userInput = "-s 1 s/1000 e/1100";
        assertParseFailure(parser, userInput, expectedMessage2);
    }

    @Test
    public void parse_sessionEditNoFieldSpecified_failure() {
        assertParseFailure(parser, "-s 1",
                String.format(Messages.MESSAGE_MISSING_PREFIX, EditCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_sessionEditInvalidIndex_failure() {
        // negative index
        assertParseFailure(parser, "-s -1" + VALID_DAY_DESC + VALID_START_DESC + VALID_END_DESC,
                MESSAGE_INVALID_INDEX);

        // zero index
        assertParseFailure(parser, "-s 0" + VALID_DAY_DESC + VALID_START_DESC + VALID_END_DESC,
                MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_sessionEditInvalidDay_failure() {
        assertParseFailure(parser, "-s 1" + INVALID_DAY_DESC + VALID_START_DESC + VALID_END_DESC,
                Session.MESSAGE_DAY_CONSTRAINTS);
    }

    @Test
    public void parse_sessionEditInvalidStart_failure() {
        assertParseFailure(parser, "-s 1" + VALID_DAY_DESC + INVALID_START_DESC + VALID_END_DESC,
                Session.MESSAGE_TIME_FORMAT_CONSTRAINTS);
    }

    @Test
    public void parse_sessionEditInvalidEnd_failure() {
        assertParseFailure(parser, "-s 1" + VALID_DAY_DESC + VALID_START_DESC + INVALID_END_DESC,
                Session.MESSAGE_TIME_FORMAT_CONSTRAINTS);
    }

    @Test
    public void parse_contactEditWithStudyYear_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + STUDY_YEAR_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withStudyYear(VALID_STUDY_YEAR_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditWithInvalidStudyYear_failure() {
        assertParseFailure(parser, "-c 1" + INVALID_STUDY_YEAR_DESC, StudyYear.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_contactEditAllFieldsIncludingStudyYear_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + NAME_DESC_AMY + STUDY_YEAR_DESC_AMY
                + PHONE_DESC_BOB + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(VALID_NAME_AMY)
                .withStudyYear(VALID_STUDY_YEAR_AMY)
                .withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_AMY)
                .build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditDuplicateStudyYear_failure() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + STUDY_YEAR_DESC_AMY + STUDY_YEAR_DESC_BOB;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STUDY_YEAR));
    }

    @Test
    public void parse_contactEditWithValidSubject_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + " sub/MATH";

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("MATH"));
        descriptor.setSubjects(subjects);

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditWithInvalidSubject_failure() {
        String userInput = "-c 1 sub/INVALID";
        assertParseFailure(parser, userInput, AddSubjectCommand.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_contactEditWithMultipleSubjects_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + " sub/MATH sub/ENG sub/SCI";

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("MATH"));
        subjects.add(new Tag("ENG"));
        subjects.add(new Tag("SCI"));
        descriptor.setSubjects(subjects);

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditWithEmptySubject_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + " sub/   " + NAME_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setName(new Name(VALID_NAME_AMY));
        descriptor.setSubjects(new HashSet<>()); // Empty set to clear all subjects

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditClearAllSubjects_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + " sub/";

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setSubjects(new HashSet<>()); // Empty set to clear all subjects

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditStudyYearAndSubjects_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + STUDY_YEAR_DESC_AMY + " sub/MATH sub/PHY";

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setStudyYear(new StudyYear(VALID_STUDY_YEAR_AMY));
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("MATH"));
        subjects.add(new Tag("PHY"));
        descriptor.setSubjects(subjects);

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditInvalidStudyYearFollowedByValidField_failure() {
        assertParseFailure(parser, "-c 1" + INVALID_STUDY_YEAR_DESC + NAME_DESC_AMY,
                StudyYear.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_largeValidIndex_success() {
        Index targetIndex = Index.fromOneBased(999999);
        String userInput = "-c " + targetIndex.getOneBased() + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_sessionEditEmptyDay_failure() {
        assertParseFailure(parser, "-s 1 d/ s/1000 e/1100",
                Session.MESSAGE_DAY_CONSTRAINTS);
    }

    @Test
    public void parse_sessionEditEmptyStart_failure() {
        assertParseFailure(parser, "-s 1 d/MON s/ e/1100",
                Session.MESSAGE_TIME_FORMAT_CONSTRAINTS);
    }

    @Test
    public void parse_sessionEditEmptyEnd_failure() {
        assertParseFailure(parser, "-s 1 d/MON s/1000 e/",
                Session.MESSAGE_TIME_FORMAT_CONSTRAINTS);
    }

    @Test
    public void parse_contactEditMultipleInvalidValues_failure() {
        // Multiple invalid values - first invalid should be reported
        assertParseFailure(parser, "-c 1" + INVALID_STUDY_YEAR_DESC + INVALID_PHONE_DESC + INVALID_EMAIL_DESC,
                StudyYear.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_contactEditDuplicateStudyYearAndPhone_failure() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + STUDY_YEAR_DESC_AMY + STUDY_YEAR_DESC_BOB
                + PHONE_DESC_AMY + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STUDY_YEAR, PREFIX_PHONE));
    }

    @Test
    public void parse_contactEditValidSubjectCodes_success() {
        // Test various valid subject codes
        Index targetIndex = INDEX_FIRST_PERSON;
        String[] validSubjects = {"MATH", "ENG", "SCI", "PHY", "CHEM", "BIO", "HIST", "GEOG",
                                  "LIT", "CHI", "MALAY", "TAMIL", "POA", "ECONS", "ART", "MUSIC", "COMSCI"};

        for (String subject : validSubjects) {
            String userInput = "-c " + targetIndex.getOneBased() + " sub/" + subject;
            EditPersonDescriptor descriptor = new EditPersonDescriptor();
            Set<Tag> subjects = new HashSet<>();
            subjects.add(new Tag(subject));
            descriptor.setSubjects(subjects);
            EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
            assertParseSuccess(parser, userInput, expectedCommand);
        }
    }

    @Test
    public void parse_sessionEditMultipleSessionsWithDifferentDays_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-s " + targetIndex.getOneBased()
                + " d/TUE s/0900 e/1000 d/THU s/1300 e/1400 d/FRI s/1500 e/1600";

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        Set<Tag> sessions = new HashSet<>();
        Session session1 = new Session("TUE", "0900", "1000");
        Session session2 = new Session("THU", "1300", "1400");
        Session session3 = new Session("FRI", "1500", "1600");
        sessions.add(new SessionTag(session1.toString(), session1));
        sessions.add(new SessionTag(session2.toString(), session2));
        sessions.add(new SessionTag(session3.toString(), session3));
        descriptor.setSessions(sessions);

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditCombinationOfAllFields_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = "-c " + targetIndex.getOneBased() + NAME_DESC_BOB + STUDY_YEAR_DESC_BOB
                + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + " sub/MATH sub/ENG";

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setName(new Name(VALID_NAME_BOB));
        descriptor.setStudyYear(new StudyYear(VALID_STUDY_YEAR_BOB));
        descriptor.setPhone(new Phone(VALID_PHONE_BOB));
        descriptor.setEmail(new Email(VALID_EMAIL_BOB));
        descriptor.setAddress(new Address(VALID_ADDRESS_BOB));
        Set<Tag> subjects = new HashSet<>();
        subjects.add(new Tag("MATH"));
        subjects.add(new Tag("ENG"));
        descriptor.setSubjects(subjects);

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_contactEditDuplicateEmptySubjects_failure() {
        // Test with two empty subject prefixes
        String userInput = "-c 1 sub/ sub/";
        assertParseFailure(parser, userInput, EditCommand.MESSAGE_DUPLICATE_SUBJECT_CLEAR);
    }

    @Test
    public void parse_contactEditMultipleEmptySubjects_failure() {
        // Test with three empty subject prefixes
        String userInput = "-c 1 sub/ sub/ sub/";
        assertParseFailure(parser, userInput, EditCommand.MESSAGE_DUPLICATE_SUBJECT_CLEAR);
    }

    @Test
    public void parse_contactEditMixedEmptyAndNonEmptySubjects_failure() {
        // Test conflicting operation: trying to clear and add at the same time
        String userInput = "-c 1 sub/ sub/MATH";
        assertParseFailure(parser, userInput, EditCommand.MESSAGE_CONFLICTING_SUBJECT_OPERATION);
    }

    @Test
    public void parse_contactEditMixedNonEmptyAndEmptySubjects_failure() {
        // Test conflicting operation in reverse order
        String userInput = "-c 1 sub/MATH sub/";
        assertParseFailure(parser, userInput, EditCommand.MESSAGE_CONFLICTING_SUBJECT_OPERATION);
    }

    @Test
    public void parse_sessionEditClearAllSessions_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = "-s " + targetIndex.getOneBased() + " clear/";

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setSessions(new HashSet<>()); // Empty set to clear all sessions

        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_sessionEditClearWithTextAfter_failure() {
        String userInput = "-s 1 clear/text";
        assertParseFailure(parser, userInput,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_sessionEditClearWithAdditionalContent_failure() {
        String userInput = "-s 1 clear/ extra";
        assertParseFailure(parser, userInput,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_sessionEditClearWithSessionPrefixes_failure() {
        String userInput = "-s 1 clear/ d/MON s/0900 e/1100";
        assertParseFailure(parser, userInput,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_sessionEditClearWithSessionPrefixesReverse_failure() {
        String userInput = "-s 1 d/MON s/0900 e/1100 clear/";
        assertParseFailure(parser, userInput,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_sessionEditClearNoIndex_failure() {
        String userInput = "-s clear/";
        assertParseFailure(parser, userInput,
                String.format(Messages.MESSAGE_MISSING_INDEX, EditCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_sessionEditClearInvalidIndex_failure() {
        // negative index
        assertParseFailure(parser, "-s -1 clear/", MESSAGE_INVALID_INDEX);

        // zero index
        assertParseFailure(parser, "-s 0 clear/", MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_sessionEditClearWithOnlySessionPrefix_failure() {
        // clear/ mixed with just one session prefix should fail
        String userInput = "-s 1 clear/ d/MON";
        assertParseFailure(parser, userInput,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }
}
