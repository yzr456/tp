package seedu.address.logic.parser;

import static seedu.address.logic.commands.AddSubjectCommand.SUBJECT_MESSAGE_CONSTRAINTS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.Optional;

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
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddSubjectCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_SUBJECT);

        if (map.getPreamble().isBlank()) {
            throw new ParseException(Messages.MESSAGE_ARGUMENT_ERROR);
        }

        if (map.getValue(PREFIX_SUBJECT).isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_MISSING_PARAMETER, AddSubjectCommand.MESSAGE_USAGE));
        }

        Optional<String> rawOpt = map.getValue(PREFIX_SUBJECT);
        if (rawOpt.isEmpty() || rawOpt.get().trim().isEmpty()) {
            throw new ParseException(Messages.MESSAGE_ARGUMENT_ERROR);
        }

        Index index = ParserUtil.parseIndex(map.getPreamble());
        String raw = map.getValue(PREFIX_SUBJECT).get();

        Subject subject = Subject.of(raw);
        if (subject == null) {
            throw new ParseException(SUBJECT_MESSAGE_CONSTRAINTS);
        }

        Tag subjectTag = new Tag(subject.name());

        return new AddSubjectCommand(index, subjectTag);
    }

}
