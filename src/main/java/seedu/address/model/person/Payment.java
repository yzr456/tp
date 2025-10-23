package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a Person's payment status in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidStatus(String)}
 */
public class Payment {

    public static final String MESSAGE_CONSTRAINTS_STATUS =
            "Payment status must be one of: PENDING, PAID, OVERDUE";
    public static final String MESSAGE_CONSTRAINTS_DAY =
            "Billing start day must be between 1-31";
    public static final int DEFAULT_BILLING_START_DAY = 1;
    public static final String VALIDATION_REGEX = "^(PENDING|PAID|OVERDUE)$";

    private final String status;
    private final int billingStartDay;
    private final LocalDate statusSetDate; // Date when status was last updated

    /**
     * Constructs a {@code Payment} with status only.
     * Billing start day defaults to 1.
     *
     * @param status A valid payment status.
     */
    public Payment(String status) {
        requireNonNull(status);
        String normalizedStatus = status.toUpperCase().trim();
        checkArgument(isValidStatus(normalizedStatus), MESSAGE_CONSTRAINTS_STATUS);
        this.status = normalizedStatus;
        this.billingStartDay = DEFAULT_BILLING_START_DAY;
        this.statusSetDate = LocalDate.now();
    }

    /**
     * Constructs a {@code Payment} with status and billing start day.
     *
     * @param status A valid payment status.
     * @param billingStartDay A valid day between 1-31.
     */
    public Payment(String status, int billingStartDay) {
        requireNonNull(status);
        String normalizedStatus = status.toUpperCase().trim();
        checkArgument(isValidStatus(normalizedStatus), MESSAGE_CONSTRAINTS_STATUS);
        checkArgument(isValidBillingDay(billingStartDay), MESSAGE_CONSTRAINTS_DAY);
        this.status = normalizedStatus;
        this.billingStartDay = billingStartDay;
        this.statusSetDate = LocalDate.now();
    }

    /**
     * Returns true if a given string is a valid payment status.
     */
    public static boolean isValidStatus(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given day is a valid billing start day.
     */
    public static boolean isValidBillingDay(int day) {
        return day >= 1 && day <= 31;
    }

    /**
     * Returns the number of days overdue if status is OVERDUE.
     * Calculates based on the billing start day and current date.
     * Returns 0 if not overdue.
     */
    public int getDaysOverdue() {
        if (!status.equals("OVERDUE")) {
            return 0;
        }

        LocalDate now = LocalDate.now();
        LocalDate billingDate = calculateBillingDate(statusSetDate, billingStartDay);

        // Calculate days between billing date and now
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(billingDate, now);
        return Math.max(0, (int) daysBetween);
    }

    /**
     * Calculates the billing date based on the status set date and billing start day.
     */
    private LocalDate calculateBillingDate(LocalDate fromDate, int billingDay) {
        LocalDate billingDate = fromDate.withDayOfMonth(Math.min(billingDay, fromDate.lengthOfMonth()));

        // If we're past the billing day this month, it refers to this month's billing
        // Otherwise it refers to last month's billing
        if (fromDate.getDayOfMonth() < billingDay) {
            billingDate = billingDate.minusMonths(1);
            billingDate = billingDate.withDayOfMonth(Math.min(billingDay, billingDate.lengthOfMonth()));
        }

        return billingDate;
    }

    @Override
    public String toString() {
        if (status.equals("OVERDUE")) {
            int daysOverdue = getDaysOverdue();
            return status + " (" + daysOverdue + " days)";
        }
        return status;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Payment)) {
            return false;
        }

        Payment otherPayment = (Payment) other;
        return status.equals(otherPayment.status)
                && billingStartDay == otherPayment.billingStartDay
                && Objects.equals(statusSetDate, otherPayment.statusSetDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, billingStartDay, statusSetDate);
    }
}
