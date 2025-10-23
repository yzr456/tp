package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Payment;

/**
 * Jackson-friendly version of {@link Payment}.
 */
class JsonAdaptedPayment {

    private final String status;
    private final int billingStartDay;

    /**
     * Constructs a {@code JsonAdaptedPayment} with the given payment details.
     */
    @JsonCreator
    public JsonAdaptedPayment(@JsonProperty("status") String status,
                              @JsonProperty("billingStartDay") int billingStartDay) {
        this.status = status;
        this.billingStartDay = billingStartDay;
    }

    /**
     * Converts a given {@code Payment} into this class for Jackson use.
     */
    public JsonAdaptedPayment(Payment source) {
        status = source.toString().split(" ")[0]; // Get just the status part (e.g., "PAID", "PENDING", "OVERDUE")
        billingStartDay = source.getBillingStartDay();
    }

    /**
     * Converts this Jackson-friendly adapted payment object into the model's {@code Payment} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted payment.
     */
    public Payment toModelType() throws IllegalValueException {
        if (status == null) {
            throw new IllegalValueException(Payment.MESSAGE_CONSTRAINTS_STATUS);
        }
        if (!Payment.isValidStatus(status)) {
            throw new IllegalValueException(Payment.MESSAGE_CONSTRAINTS_STATUS);
        }
        if (!Payment.isValidBillingDay(billingStartDay)) {
            throw new IllegalValueException(Payment.MESSAGE_CONSTRAINTS_DAY);
        }
        return new Payment(status, billingStartDay);
    }
}
