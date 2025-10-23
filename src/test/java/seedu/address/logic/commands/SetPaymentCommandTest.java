package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Payment;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for SetPaymentCommand.
 */
public class SetPaymentCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_setPaymentStatusOnly_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withPayment("PAID", 1).build();

        SetPaymentCommand setPaymentCommand = new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID");

        String expectedMessage = String.format(SetPaymentCommand.MESSAGE_SET_PAYMENT_SUCCESS,
                firstPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(setPaymentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_setPaymentStatusAndBillingDay_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withPayment("OVERDUE", 15).build();

        SetPaymentCommand setPaymentCommand = new SetPaymentCommand(INDEX_FIRST_PERSON, "OVERDUE", 15);

        String expectedMessage = String.format(SetPaymentCommand.MESSAGE_SET_PAYMENT_SUCCESS,
                firstPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(setPaymentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        SetPaymentCommand setPaymentCommand = new SetPaymentCommand(outOfBoundIndex, "PAID");

        assertCommandFailure(setPaymentCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidStatus_throwsIllegalArgumentException() {
        // This test verifies that Payment constructor throws IllegalArgumentException for invalid status
        try {
            new Payment("INVALID_STATUS");
            assertTrue(false, "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(Payment.MESSAGE_CONSTRAINTS_STATUS, e.getMessage());
        }
    }

    @Test
    public void execute_invalidBillingDay_throwsIllegalArgumentException() {
        // Test billing day out of range (> 31)
        try {
            new Payment("PAID", 32);
            assertTrue(false, "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(Payment.MESSAGE_CONSTRAINTS_DAY, e.getMessage());
        }

        // Test billing day out of range (< 1)
        try {
            new Payment("PAID", 0);
            assertTrue(false, "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(Payment.MESSAGE_CONSTRAINTS_DAY, e.getMessage());
        }
    }

    @Test
    public void equals() {
        SetPaymentCommand setPaymentFirstCommand = new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID");
        SetPaymentCommand setPaymentSecondCommand = new SetPaymentCommand(INDEX_SECOND_PERSON, "PAID");
        SetPaymentCommand setPaymentDifferentStatus = new SetPaymentCommand(INDEX_FIRST_PERSON, "PENDING");
        SetPaymentCommand setPaymentWithBillingDay = new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID", 15);

        // same object -> returns true
        assertTrue(setPaymentFirstCommand.equals(setPaymentFirstCommand));

        // same values -> returns true
        SetPaymentCommand setPaymentFirstCommandCopy = new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID");
        assertTrue(setPaymentFirstCommand.equals(setPaymentFirstCommandCopy));

        // different types -> returns false
        assertFalse(setPaymentFirstCommand.equals(1));

        // null -> returns false
        assertFalse(setPaymentFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(setPaymentFirstCommand.equals(setPaymentSecondCommand));

        // different status -> returns false
        assertFalse(setPaymentFirstCommand.equals(setPaymentDifferentStatus));

        // different billing day -> returns false
        assertFalse(setPaymentFirstCommand.equals(setPaymentWithBillingDay));

        // same values with billing day -> returns true
        SetPaymentCommand setPaymentWithBillingDayCopy = new SetPaymentCommand(INDEX_FIRST_PERSON, "PAID", 15);
        assertTrue(setPaymentWithBillingDay.equals(setPaymentWithBillingDayCopy));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        SetPaymentCommand setPaymentCommand = new SetPaymentCommand(targetIndex, "PAID");
        String expected = SetPaymentCommand.class.getCanonicalName()
                + "{index=" + targetIndex + ", status=PAID, billingStartDay=null}";
        assertEquals(expected, setPaymentCommand.toString());

        SetPaymentCommand setPaymentCommandWithDay = new SetPaymentCommand(targetIndex, "PAID", 15);
        String expectedWithDay = SetPaymentCommand.class.getCanonicalName()
                + "{index=" + targetIndex + ", status=PAID, billingStartDay=15}";
        assertEquals(expectedWithDay, setPaymentCommandWithDay.toString());
    }
}
