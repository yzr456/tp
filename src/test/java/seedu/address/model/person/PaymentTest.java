package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class PaymentTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Payment(null));
        assertThrows(NullPointerException.class, () -> new Payment(null, 15));
    }

    @Test
    public void constructor_invalidStatus_throwsIllegalArgumentException() {
        String invalidStatus = "INVALID";
        assertThrows(IllegalArgumentException.class, () -> new Payment(invalidStatus));
        assertThrows(IllegalArgumentException.class, () -> new Payment(invalidStatus, 15));
    }

    @Test
    public void constructor_invalidBillingDay_throwsIllegalArgumentException() {
        // Billing day < 1
        assertThrows(IllegalArgumentException.class, () -> new Payment("PAID", 0));
        assertThrows(IllegalArgumentException.class, () -> new Payment("PAID", -1));

        // Billing day > 31
        assertThrows(IllegalArgumentException.class, () -> new Payment("PAID", 32));
        assertThrows(IllegalArgumentException.class, () -> new Payment("PAID", 100));
    }

    @Test
    public void constructor_validStatusOnly_success() {
        Payment payment = new Payment("PAID");
        assertEquals(Payment.DEFAULT_BILLING_START_DAY, payment.getBillingStartDay());
    }

    @Test
    public void constructor_validStatusAndBillingDay_success() {
        Payment payment = new Payment("PAID", 15);
        assertEquals(15, payment.getBillingStartDay());
    }

    @Test
    public void constructor_caseInsensitiveStatus_success() {
        // Lowercase
        Payment paymentLower = new Payment("paid");
        assertEquals("PAID", paymentLower.toString());

        // Mixed case
        Payment paymentMixed = new Payment("PeNdInG");
        assertEquals("PENDING", paymentMixed.toString());

        // Uppercase
        Payment paymentUpper = new Payment("OVERDUE");
        assertEquals("OVERDUE", paymentUpper.toString().substring(0, 7)); // Extract status part
    }

    @Test
    public void isValidStatus() {
        // Valid statuses
        assertTrue(Payment.isValidStatus("PENDING"));
        assertTrue(Payment.isValidStatus("PAID"));
        assertTrue(Payment.isValidStatus("OVERDUE"));

        // Case insensitive
        assertTrue(Payment.isValidStatus("pending"));
        assertTrue(Payment.isValidStatus("paid"));
        assertTrue(Payment.isValidStatus("overdue"));
        assertTrue(Payment.isValidStatus("PeNdInG"));

        // Invalid statuses
        assertFalse(Payment.isValidStatus(""));
        assertFalse(Payment.isValidStatus("INVALID"));
        assertFalse(Payment.isValidStatus("NOT_A_STATUS"));
        assertFalse(Payment.isValidStatus("123"));
    }

    @Test
    public void isValidBillingDay() {
        // Valid billing days
        assertTrue(Payment.isValidBillingDay(1));
        assertTrue(Payment.isValidBillingDay(15));
        assertTrue(Payment.isValidBillingDay(31));

        // Invalid billing days
        assertFalse(Payment.isValidBillingDay(0));
        assertFalse(Payment.isValidBillingDay(-1));
        assertFalse(Payment.isValidBillingDay(32));
        assertFalse(Payment.isValidBillingDay(100));
    }

    @Test
    public void getDaysOverdue_nonOverdueStatus_returnsZero() {
        Payment pendingPayment = new Payment("PENDING");
        assertEquals(0, pendingPayment.getDaysOverdue());

        Payment paidPayment = new Payment("PAID");
        assertEquals(0, paidPayment.getDaysOverdue());
    }

    @Test
    public void getDaysOverdue_overdueStatus_returnsNonZeroPositiveNumber() {
        Payment overduePayment = new Payment("OVERDUE");
        // Days overdue should be >= 0
        assertTrue(overduePayment.getDaysOverdue() > 0);
    }

    @Test
    public void getDaysOverdue_overdueStatusWithCustomBillingDay_calculatesCorrectly() {
        // Test with various billing days to ensure calculation logic works
        Payment overduePayment15 = new Payment("OVERDUE", 15);
        assertTrue(overduePayment15.getDaysOverdue() > 0);

        Payment overduePayment31 = new Payment("OVERDUE", 31);
        assertTrue(overduePayment31.getDaysOverdue() > 0);

        // Test edge case: billing day at end of month
        Payment overduePaymentLastDay = new Payment("OVERDUE", 31);
        assertTrue(overduePaymentLastDay.getDaysOverdue() > 0);
    }

    @Test
    public void getDaysOverdue_billingDayExceedsMonthLength_handlesCorrectly() {
        // Test case where billing day is 31 but month has fewer days
        // This tests the Math.min() logic in calculateBillingDate
        Payment overduePayment = new Payment("OVERDUE", 31);
        int daysOverdue = overduePayment.getDaysOverdue();
        assertTrue(daysOverdue > 0);
    }

    @Test
    public void calculateBillingDate_variousScenarios() {
        // Test that overdue payment calculates days correctly regardless of current date
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();

        // Create overdue payment with billing day before current day
        if (currentDay > 1) {
            Payment overduePayment = new Payment("OVERDUE", 1);
            int daysOverdue = overduePayment.getDaysOverdue();
            assertTrue(daysOverdue > 0);
        }

        // Create overdue payment with billing day after current day
        if (currentDay < 28) { // Use 28 to avoid month-end issues
            Payment overduePayment = new Payment("OVERDUE", 28);
            int daysOverdue = overduePayment.getDaysOverdue();
            assertTrue(daysOverdue > 0);
        }
    }

    @Test
    public void getDaysOverdue_overdueStatusOnBillingDay_calculatesFromLastMonth() {
        // Special case: When status is OVERDUE and billing day equals today
        // The calculation should reference last month's billing
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();

        // Create overdue payment with billing day equal to today
        Payment overduePayment = new Payment("OVERDUE", currentDay);
        int daysOverdue = overduePayment.getDaysOverdue();

        // Should be positive since it references last month's billing cycle
        assertTrue(daysOverdue > 0,
                "Days overdue should be positive when billing day equals today for OVERDUE status");

        // Days overdue should be roughly the number of days in the last month
        // (accounting for variable month lengths)
        assertTrue(daysOverdue >= 28 && daysOverdue <= 31,
                "Days overdue should be approximately one month when OVERDUE on the billing day");
    }


    @Test
    public void getBillingStartDay_success() {
        Payment paymentDefault = new Payment("PAID");
        assertEquals(1, paymentDefault.getBillingStartDay());

        Payment paymentCustom = new Payment("PAID", 15);
        assertEquals(15, paymentCustom.getBillingStartDay());

        Payment paymentBoundary = new Payment("PAID", 31);
        assertEquals(31, paymentBoundary.getBillingStartDay());
    }

    @Test
    public void toString_pendingStatus() {
        Payment payment = new Payment("PENDING");
        assertEquals("PENDING", payment.toString());
    }

    @Test
    public void toString_paidStatus() {
        Payment payment = new Payment("PAID");
        assertEquals("PAID", payment.toString());
    }

    @Test
    public void toString_overdueStatus_includesDaysOverdue() {
        Payment payment = new Payment("OVERDUE");
        String result = payment.toString();
        assertTrue(result.startsWith("OVERDUE ("));
        assertTrue(result.endsWith(" days)"));
    }

    @Test
    public void toString_overdueStatusWithCustomBillingDay_includesDaysOverdue() {
        Payment payment = new Payment("OVERDUE", 15);
        String result = payment.toString();
        assertTrue(result.startsWith("OVERDUE ("));
        assertTrue(result.contains("days"));
        assertTrue(result.endsWith(")"));
    }

    @Test
    public void equals() {
        Payment payment1 = new Payment("PAID", 15);
        Payment payment3 = new Payment("PENDING", 15);
        Payment payment4 = new Payment("PAID", 20);
        Payment payment5 = new Payment("PAID");

        // Same object
        assertEquals(payment1, payment1);

        // Note: Two Payment objects created at different times will have different statusSetDate,
        // so they won't be equal even with same status and billingStartDay.
        // This is correct behavior as Payment is immutable and includes timestamp.

        // Different status
        assertNotEquals(payment1, payment3);

        // Different billing day
        assertNotEquals(payment1, payment4);

        // Different billing day (default vs custom)
        assertNotEquals(payment1, payment5);

        // Null
        assertFalse(payment1.equals(null));

        // Different type
        assertFalse(payment1.equals("PAID"));
    }

    @Test
    public void hashCode_consistency() {
        Payment payment = new Payment("PAID", 15);
        int hashCode1 = payment.hashCode();
        int hashCode2 = payment.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void hashCode_differentPayments() {
        Payment payment1 = new Payment("PAID", 15);
        Payment payment2 = new Payment("PENDING", 15);
        Payment payment3 = new Payment("PAID", 20);

        // Different status should generally produce different hash codes
        assertNotEquals(payment1.hashCode(), payment2.hashCode());

        // Different billing day should generally produce different hash codes
        assertNotEquals(payment1.hashCode(), payment3.hashCode());
    }

    @Test
    public void testEnumValues() {
        // Test that enum values exist
        assertEquals(Payment.PaymentStatus.PENDING, Payment.PaymentStatus.valueOf("PENDING"));
        assertEquals(Payment.PaymentStatus.PAID, Payment.PaymentStatus.valueOf("PAID"));
        assertEquals(Payment.PaymentStatus.OVERDUE, Payment.PaymentStatus.valueOf("OVERDUE"));
    }

    @Test
    public void testEnumName() {
        // Test enum names
        assertEquals("PENDING", Payment.PaymentStatus.PENDING.name());
        assertEquals("PAID", Payment.PaymentStatus.PAID.name());
        assertEquals("OVERDUE", Payment.PaymentStatus.OVERDUE.name());
    }
}
