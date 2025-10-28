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

    /**
     * Represents the possible payment statuses.
     */
    public enum PaymentStatus {
        PENDING,
        PAID,
        OVERDUE
    }

    public static final String MESSAGE_CONSTRAINTS_STATUS =
            "Payment status cannot be blank and must be one of: PENDING, PAID, OVERDUE";
    public static final String MESSAGE_CONSTRAINTS_DAY =
            "Billing start day cannot be blank and must be an integer between 1-31.";
    public static final int DEFAULT_BILLING_START_DAY = 1;

    private final PaymentStatus status;
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
        this.status = parseStatus(status);
        this.billingStartDay = DEFAULT_BILLING_START_DAY;
        this.statusSetDate = LocalDate.now();
    }

    /**
     * Constructs a {@code Payment} with status and billing start day.
     *
     * @param status A valid payment status.
     * @param billingStartDay A valid integer between 1-31.
     */
    public Payment(String status, int billingStartDay) {
        requireNonNull(status);
        checkArgument(isValidBillingDay(billingStartDay), MESSAGE_CONSTRAINTS_DAY);
        this.status = parseStatus(status);
        this.billingStartDay = billingStartDay;
        this.statusSetDate = LocalDate.now();
    }

    /**
     * Parses a string to a PaymentStatus enum.
     * @throws IllegalArgumentException if the string is not a valid payment status.
     */
    private static PaymentStatus parseStatus(String status) {
        String normalizedStatus = status.toUpperCase().trim();
        try {
            return PaymentStatus.valueOf(normalizedStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS_STATUS);
        }
    }

    /**
     * Returns true if a given string is a valid payment status.
     */
    public static boolean isValidStatus(String test) {
        try {
            PaymentStatus.valueOf(test.toUpperCase().trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns true if a given day is a valid billing start day.
     */
    public static boolean isValidBillingDay(int day) {
        return day >= 1 && day <= 31;
    }

    /**
     * Returns the billing start day.
     */
    public int getBillingStartDay() {
        return billingStartDay;
    }

    /**
     * Returns a description of the billing start day.
     */
    public String getBillingStartDayDescription() {
        if (billingStartDay >= 29) {
            return "Day " + billingStartDay + " of each month (or last day of month if unavailable)";
        }
        return "Day " + billingStartDay + " of each month";
    }

    /**
     * Returns the number of days overdue if status is OVERDUE.
     * Calculates based on the billing start day and current date.
     * Returns 0 if not overdue.
     */
    public int getDaysOverdue() {
        if (status != PaymentStatus.OVERDUE) {
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
     * For OVERDUE status, references the most recent past billing cycle.
     * Special case: If setting to OVERDUE on the billing day itself, references last month's billing.
     */
    private LocalDate calculateBillingDate(LocalDate fromDate, int billingDay) {
        LocalDate billingDate = fromDate.withDayOfMonth(Math.min(billingDay, fromDate.lengthOfMonth()));

        // If we're before the billing day OR (status is OVERDUE and today IS the billing day),
        // the most recent billing was last month
        if (fromDate.getDayOfMonth() < billingDay
                || (status == PaymentStatus.OVERDUE && fromDate.getDayOfMonth() == billingDay)) {
            billingDate = billingDate.minusMonths(1);
            billingDate = billingDate.withDayOfMonth(Math.min(billingDay, billingDate.lengthOfMonth()));
        }
        // Otherwise, the most recent billing day is this month (we've already passed it)

        return billingDate;
    }

    @Override
    public String toString() {
        if (status == PaymentStatus.OVERDUE) {
            int daysOverdue = getDaysOverdue();
            return status.name() + " (" + daysOverdue + " days)";
        }
        return status.name();
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
