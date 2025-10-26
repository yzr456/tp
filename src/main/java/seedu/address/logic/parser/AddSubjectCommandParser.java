package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddSubjectCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.subject.Subject;

/**
 * Parses input arguments and creates a new AddSubjectCommand object
 */
public class AddSubjectCommandParser implements Parser<AddSubjectCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddSubjectCommand
     * and returns a AddSubjectCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddSubjectCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_SUBJECT);

        if (argMultimap.getPreamble().isBlank()) {
            throw new ParseException(Messages.MESSAGE_MISSING_INDEX);
        }

        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());

        List<String> subjectValues = argMultimap.getAllValues(PREFIX_SUBJECT);
        if (subjectValues.isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_MISSING_PREFIX, AddSubjectCommand.MESSAGE_USAGE));
        }

        Set<Tag> subjectTags = new HashSet<>();
        for (String raw : subjectValues) {

            Subject subject = Subject.of(raw);
            if (subject == null) {
                throw new ParseException(AddSubjectCommand.MESSAGE_CONSTRAINTS);
            }

            Tag subjectTag = new Tag(subject.name());

            if (subjectTags.contains(subjectTag)) {
                throw new ParseException(AddSubjectCommand.MESSAGE_DUPLICATE_SUBJECT_IN_COMMAND);
            }
            subjectTags.add(subjectTag);
        }

        return new AddSubjectCommand(index, subjectTags);
    }

}
