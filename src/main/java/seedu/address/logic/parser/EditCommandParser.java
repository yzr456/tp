package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDY_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
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
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        // Parse flag
        String flag = "";
        String remainingArgs = trimmedArgs;

        if (trimmedArgs.startsWith("-")) {
            int spaceIndex = trimmedArgs.indexOf(' ');
            if (spaceIndex == -1) {
                throw new ParseException(EditCommand.MESSAGE_MISSING_FLAG);
            }
            flag = trimmedArgs.substring(0, spaceIndex).trim();
            remainingArgs = trimmedArgs.substring(spaceIndex).trim();
        } else {
            throw new ParseException(EditCommand.MESSAGE_MISSING_FLAG);
        }

        // Validate flag
        if (!flag.equals("-c") && !flag.equals("-s")) {
            throw new ParseException(EditCommand.MESSAGE_INVALID_FLAG);
        }

        // For now, only -c is supported
        if (flag.equals("-s")) {
            throw new ParseException("Session editing is not yet implemented.");
        }

        // Parse contact edit command
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(remainingArgs, PREFIX_NAME, PREFIX_STUDY_YEAR,
                PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_SUBJECT);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_STUDY_YEAR,
                PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
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

        // Parse subjects
        parseSubjectsForEdit(argMultimap.getAllValues(PREFIX_SUBJECT)).ifPresent(editPersonDescriptor::setSubjects);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> subjects} into a {@code Set<Tag>} if {@code subjects} is non-empty.
     * Validates that each subject is a valid subject code.
     */
    private Optional<Set<Tag>> parseSubjectsForEdit(Collection<String> subjects) throws ParseException {
        assert subjects != null;

        if (subjects.isEmpty()) {
            return Optional.empty();
        }

        Set<Tag> subjectTags = new HashSet<>();
        for (String subjectStr : subjects) {
            if (subjectStr.trim().isEmpty()) {
                continue;
            }

            Subject subject = Subject.of(subjectStr);
            if (subject == null) {
                throw new ParseException(
                    "Invalid subject provided. The Subject provided must be a valid subject code: "
                    + "MATH, ENG, SCI, PHY, CHEM, BIO, HIST, GEOG, LIT, CHI, MALAY, TAMIL, "
                    + "POA, ECONS, ART, MUSIC, COMSCI");
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

}
