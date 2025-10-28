package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Payment;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Session;
import seedu.address.model.person.StudyYear;
import seedu.address.model.tag.SessionTag;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_DURATION = "Duration is not an unsigned integer";
    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code duration} into an {@code Integer} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified duration is invalid (not non-zero unsigned integer).
     */
    public static int parseDuration(String duration) throws ParseException {
        String trimmedDuration = duration.trim();
        if (!StringUtil.isUnsignedInteger(trimmedDuration)) {
            throw new ParseException(MESSAGE_INVALID_DURATION);
        }
        return Integer.parseInt(trimmedDuration);
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     * Duplicate whitespaces are removed
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim().replaceAll("\\s+", " ");
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String studyYear} into a {@code StudyYear}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code studyYear} is invalid.
     */
    public static StudyYear parseStudyYear(String studyYear) throws ParseException {
        requireNonNull(studyYear);
        String trimmedStudyYear = studyYear.trim();
        if (!StudyYear.isValidStudyYear(trimmedStudyYear)) {
            throw new ParseException(StudyYear.MESSAGE_CONSTRAINTS);
        }
        return new StudyYear(trimmedStudyYear);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     * Duplicate whitespaces are removed
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim().replaceAll("\\s+", " ");
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses {@code day}, {@code start}, {@code end} into a {@code Session}.
     */
    public static Tag parseSessionTag(String day, String start, String end) throws ParseException {
        requireAllNonNull(day, start, end);
        String trimmedDay = day.trim();
        trimmedDay = trimmedDay.toUpperCase();
        String trimmedStart = start.trim();
        String trimmedEnd = end.trim();

        if (!Session.isValidSession(trimmedDay, trimmedStart, trimmedEnd)) {
            throw new ParseException(Session.MESSAGE_CONSTRAINTS);
        }

        Session session = new Session(trimmedDay, trimmedStart, trimmedEnd);

        return new SessionTag(session.toString(), session);
    }

    /**
     * Parses a {@code String status} and validates it as a payment status.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code status} is invalid.
     */
    public static String parsePaymentStatus(String status) throws ParseException {
        requireNonNull(status);
        String trimmedStatus = status.trim();
        if (trimmedStatus.isEmpty() || !Payment.isValidStatus(trimmedStatus)) {
            throw new ParseException(Payment.MESSAGE_CONSTRAINTS_STATUS);
        }
        return trimmedStatus;
    }

    /**
     * Parses a {@code String day} into a billing start day integer.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code day} is invalid.
     */
    public static int parseBillingDay(String day) throws ParseException {
        requireNonNull(day);
        String trimmedDay = day.trim();
        if (trimmedDay.isEmpty()) {
            throw new ParseException(Payment.MESSAGE_CONSTRAINTS_DAY);
        }

        int billingDay;
        try {
            billingDay = Integer.parseInt(trimmedDay);
        } catch (NumberFormatException e) {
            throw new ParseException(Payment.MESSAGE_CONSTRAINTS_DAY);
        }

        if (!Payment.isValidBillingDay(billingDay)) {
            throw new ParseException(Payment.MESSAGE_CONSTRAINTS_DAY);
        }

        return billingDay;
    }

    /**
     * Parses a {@code String tagStr} into a {@code Session}.
     * Expected format: "DAY START-END" (e.g., "MON 0900-1100").
     *
     * @throws ParseException if the given {@code tagStr} is invalid.
     */
    public static Session parseSessionStr(String tagStr) throws ParseException {
        requireNonNull(tagStr);
        Pattern sessionTagFormat = Pattern.compile(
                "(?<dayOfWeek>[A-Z]{3})\\s(?<start>\\d{4})\\s*-\\s*(?<end>\\d{4})");
        Matcher matcher = sessionTagFormat.matcher(tagStr);
        if (!matcher.matches()) {
            throw new ParseException(Session.MESSAGE_CONSTRAINTS);
        }
        String dayOfWeek = matcher.group("dayOfWeek");
        String start = matcher.group("start");
        String end = matcher.group("end");

        if (!Session.isValidSession(dayOfWeek, start, end)) {
            throw new ParseException(Session.MESSAGE_CONSTRAINTS);
        }
        return new Session(dayOfWeek, start, end);

    }
}
