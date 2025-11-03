package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDY_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddSubjectCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Session;
import seedu.address.model.tag.SessionTag;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.subject.Subject;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String trimmedArgs = args.trim();

        // Check for flag
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        // Parse flag
        String flag = "";
        String remainingArgs = trimmedArgs;

        if (trimmedArgs.startsWith("-")) {
            int spaceIndex = trimmedArgs.indexOf(' ');
            if (spaceIndex == -1) {
                // Check if what was provided is actually a valid flag
                String providedFlag = trimmedArgs.trim();
                if (providedFlag.equals("-c") || providedFlag.equals("-s")) {
                    throw new ParseException(EditCommand.MESSAGE_MISSING_ARGUMENTS);
                } else {
                    throw new ParseException(EditCommand.MESSAGE_MISSING_FLAG);
                }
            }
            flag = trimmedArgs.substring(0, spaceIndex).trim();
            remainingArgs = trimmedArgs.substring(spaceIndex);
        } else {
            throw new ParseException(EditCommand.MESSAGE_MISSING_FLAG);
        }

        // Validate flag
        if (!flag.equals("-c") && !flag.equals("-s")) {
            throw new ParseException(EditCommand.MESSAGE_INVALID_FLAG);
        }

        // Parse based on flag
        if (flag.equals("-c")) {
            return parseContactEdit(remainingArgs);
        } else {
            return parseSessionEdit(remainingArgs);
        }
    }

    /**
     * Parses contact edit arguments and returns an EditCommand.
     */
    private EditCommand parseContactEdit(String args) throws ParseException {
        // Preprocess to protect "s/o" pattern in names from being confused with study year prefix
        String processedArgs = preprocessSonOf(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(processedArgs, PREFIX_NAME, PREFIX_STUDY_YEAR,
                PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_SUBJECT);

        if (argMultimap.getPreamble().isBlank()) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_INDEX,
                    EditCommand.MESSAGE_USAGE));
        }

        Index index = ParserUtil.parseIndex(argMultimap.getPreamble().split("\\s+")[0]);

        if (!isAnyPrefixPresent(argMultimap, PREFIX_NAME, PREFIX_STUDY_YEAR,
                PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_SUBJECT)) {
            throw new ParseException(String.format(EditCommand.MESSAGE_NOT_EDITED));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_STUDY_YEAR,
                PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            // Restore "s/o" pattern in the name value
            String nameValue = restoreSonOf(argMultimap.getValue(PREFIX_NAME).get());
            editPersonDescriptor.setName(ParserUtil.parseName(nameValue));
        }
        if (argMultimap.getValue(PREFIX_STUDY_YEAR).isPresent()) {
            editPersonDescriptor.setStudyYear(ParserUtil.parseStudyYear(argMultimap.getValue(PREFIX_STUDY_YEAR).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }

        // Parse subjects - check for conflicting operations first
        Collection<String> subjectValues = argMultimap.getAllValues(PREFIX_SUBJECT);
        if (subjectValues.size() > 1) {
            // Check if all are empty (duplicate clear operations)
            boolean allEmpty = subjectValues.stream().allMatch(String::isEmpty);
            if (allEmpty) {
                throw new ParseException(EditCommand.MESSAGE_DUPLICATE_SUBJECT_CLEAR);
            }

            // Check for mixed operations (clear and add at the same time)
            boolean hasEmpty = subjectValues.stream().anyMatch(String::isEmpty);
            boolean hasNonEmpty = subjectValues.stream().anyMatch(s -> !s.isEmpty());
            if (hasEmpty && hasNonEmpty) {
                throw new ParseException(EditCommand.MESSAGE_CONFLICTING_SUBJECT_OPERATION);
            }
        }
        parseSubjectsForEdit(subjectValues).ifPresent(editPersonDescriptor::setSubjects);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Preprocesses the arguments string to protect "s/o" pattern from being
     * mistaken as the study year prefix. Replaces it with temporary placeholders
     * that preserve the original case.
     */
    private String preprocessSonOf(String args) {
        // Replace different case variants with unique placeholders to preserve case
        String processed = args.replace(" s/o ", " __SO_LOWER__ ");
        processed = processed.replace(" S/O ", " __SO_UPPER__ ");
        processed = processed.replace(" S/o ", " __SO_TITLE__ ");
        return processed;
    }

    /**
     * Restores the original "s/o" pattern from placeholders, preserving case.
     */
    private String restoreSonOf(String value) {
        String restored = value.replace("__SO_LOWER__", "s/o");
        restored = restored.replace("__SO_UPPER__", "S/O");
        restored = restored.replace("__SO_TITLE__", "S/o");
        return restored;
    }

    /**
     * Parses session edit arguments and returns an EditCommand.
     */
    private EditCommand parseSessionEdit(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_DAY, PREFIX_START, PREFIX_END, PREFIX_CLEAR);

        if (argMultimap.getPreamble().isBlank()) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_INDEX,
                    EditCommand.MESSAGE_USAGE));
        }

        Index index = ParserUtil.parseIndex(argMultimap.getPreamble().split("\\s+")[0]);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        // Extract only the session parameters (after the index)
        String sessionArgs = args.substring(args.indexOf(index.getOneBased() + "")
                + String.valueOf(index.getOneBased()).length()).trim();
        boolean hasClearPrefix = argMultimap.getValue(PREFIX_CLEAR).isPresent();
        if (hasClearPrefix) {
            if (!sessionArgs.equals("clear/")) {
                throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                        EditCommand.MESSAGE_USAGE));
            }
            editPersonDescriptor.setSessions(Collections.emptySet());
            return new EditCommand(index, editPersonDescriptor);
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_DAY, PREFIX_START, PREFIX_END)) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_PREFIX,
                    EditCommand.MESSAGE_USAGE));
        }

        // Check if session parameters are empty - session editing requires at least one complete triplet
        if (sessionArgs.isEmpty()) {
            throw new ParseException(EditCommand.MESSAGE_INVALID_SESSION_SEQUENCE);
        }

        // Parse sessions - enforce sequential triplet order (d/, s/, e/)
        parseSessionsForEdit(sessionArgs).ifPresent(editPersonDescriptor::setSessions);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses session definitions sequentially (d/, s/, e/ groups) into a {@code Set<Tag>} if non-empty.
     * Enforces strict triplet ordering for each session.
     * <p>
     * Throws:
     * - ParseException with EditCommand.MESSAGE_INVALID_SESSION_SEQUENCE if prefix order or structure is invalid
     * - ParseException with Session.MESSAGE_CONSTRAINTS if day/start/end values are invalid
     */
    private Optional<Set<Tag>> parseSessionsForEdit(String args) throws ParseException {
        requireNonNull(args);
        Set<Tag> sessionTags = new HashSet<>();

        // Split into tokens based on whitespace
        String[] tokens = args.trim().split("\\s+");
        String currentDay = null;
        String currentStart = null;
        String currentEnd = null;
        String expected = "d/";

        for (String token : tokens) {
            if (token.startsWith("d/")) {
                // Out-of-order or duplicate prefix error
                if (!expected.equals("d/")) {
                    throw new ParseException(EditCommand.MESSAGE_INVALID_SESSION_SEQUENCE);
                }
                currentDay = token.substring(2).trim();
                currentDay = currentDay.toUpperCase();
                if (currentDay.isEmpty()) {
                    throw new ParseException(Session.MESSAGE_DAY_CONSTRAINTS);
                }
                expected = "s/";

            } else if (token.startsWith("s/")) {
                if (!expected.equals("s/")) {
                    throw new ParseException(EditCommand.MESSAGE_INVALID_SESSION_SEQUENCE);
                }
                currentStart = token.substring(2).trim();
                if (currentStart.isEmpty()) {
                    throw new ParseException(Session.MESSAGE_TIME_FORMAT_CONSTRAINTS);
                }
                expected = "e/";

            } else if (token.startsWith("e/")) {
                if (!expected.equals("e/")) {
                    throw new ParseException(EditCommand.MESSAGE_INVALID_SESSION_SEQUENCE);
                }
                currentEnd = token.substring(2).trim();
                if (currentEnd.isEmpty()) {
                    throw new ParseException(Session.MESSAGE_TIME_FORMAT_CONSTRAINTS);
                }

                try {
                    // Let Session validate the triplet values
                    Session session = new Session(currentDay, currentStart, currentEnd);
                    sessionTags.add(new SessionTag(session.toString(), session));
                } catch (IllegalArgumentException e) {
                    // Delegate to model constraints
                    throw new ParseException(e.getMessage());
                }

                // Prepare for next possible triplet
                currentDay = currentStart = currentEnd = null;
                expected = "d/";

            } else if (!token.isBlank()) {
                // Unknown prefix or random input: structural violation
                throw new ParseException(EditCommand.MESSAGE_INVALID_SESSION_SEQUENCE);
            }
        }

        // Detect incomplete triplet
        if (!expected.equals("d/")) {
            throw new ParseException(EditCommand.MESSAGE_INVALID_SESSION_SEQUENCE);
        }

        if (sessionTags.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(sessionTags);
    }

    /**
     * Parses {@code Collection<String> subjects} into a {@code Set<Tag>} if {@code subjects} is non-empty.
     * Validates that each subject is a valid subject code.
     * If subjects contains a single completely empty string (no whitespace),
     * returns an empty set to clear all subjects.
     */
    private Optional<Set<Tag>> parseSubjectsForEdit(Collection<String> subjects) throws ParseException {
        assert subjects != null;

        if (subjects.isEmpty()) {
            return Optional.empty();
        }

        // Check if user explicitly wants to clear subjects (sub/ with completely empty value, no whitespace)
        if (subjects.size() == 1 && subjects.iterator().next().isEmpty()) {
            return Optional.of(Collections.emptySet());
        }

        Set<Tag> subjectTags = new HashSet<>();
        for (String subjectStr : subjects) {
            if (subjectStr.trim().isEmpty()) {
                continue;
            }

            Subject subject = Subject.of(subjectStr);
            if (subject == null) {
                throw new ParseException(AddSubjectCommand.MESSAGE_CONSTRAINTS);
            }
            subjectTags.add(new Tag(subject.name()));
        }

        return subjectTags.isEmpty() ? Optional.empty() : Optional.of(subjectTags);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Returns true if at least one of the prefixes contains a non-empty {@code Optional} value in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean isAnyPrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
