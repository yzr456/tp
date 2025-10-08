package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.StringUtil;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

        List<String> firstKeywords = List.of("first");
        List<String> secondKeywords = List.of("second");

        FindCommand findFirstCommand = new FindCommand(firstPredicate, firstKeywords);
        FindCommand findSecondCommand = new FindCommand(secondPredicate, secondKeywords);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true (equality is based on predicate)
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate, firstKeywords);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String input = " ";
        NameContainsKeywordsPredicate predicate = preparePredicate(input);
        List<String> keywords = List.of(input);
        FindCommand command = new FindCommand(predicate, keywords);
        expectedModel.updateFilteredPersonList(predicate);

        String quotedKeywords = StringUtil.wrapEachInQuotesAndJoin(keywords);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0, quotedKeywords);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String input = "Kurz Elle Kunz";
        NameContainsKeywordsPredicate predicate = preparePredicate(input);
        List<String> keywords = Arrays.asList(input.split("\\s+"));
        FindCommand command = new FindCommand(predicate, keywords);
        expectedModel.updateFilteredPersonList(predicate);

        String quotedKeywords = StringUtil.wrapEachInQuotesAndJoin(keywords);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3, quotedKeywords);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        List<String> keywords = List.of("keyword");
        FindCommand findCommand = new FindCommand(predicate, keywords);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}
