package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDY_YEAR;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.StudyYear;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        // Preprocess to protect "s/o" pattern in names from being confused with study year prefix
        String processedArgs = preprocessSonOf(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(processedArgs, PREFIX_NAME, PREFIX_STUDY_YEAR,
                        PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_STUDY_YEAR, PREFIX_ADDRESS, PREFIX_PHONE,
                PREFIX_EMAIL)) {
            throw new ParseException(String.format(Messages.MESSAGE_MISSING_PREFIX, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_STUDY_YEAR,
                PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        // Restore "s/o" pattern in the name value
        String nameValue = restoreSonOf(argMultimap.getValue(PREFIX_NAME).get());
        Name name = ParserUtil.parseName(nameValue);
        StudyYear studyYear = ParserUtil.parseStudyYear(argMultimap.getValue(PREFIX_STUDY_YEAR).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Set<Tag> tagList = ParserUtil.parseTags(List.of());

        Person person = new Person(name, studyYear, phone, email, address, tagList);

        return new AddCommand(person);
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
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
