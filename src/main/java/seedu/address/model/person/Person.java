package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final StudyYear studyYear;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final Payment payment;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, StudyYear studyYear, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, studyYear, phone, email, address, tags);
        this.name = name;
        this.studyYear = studyYear;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.payment = new Payment("PENDING"); // Default payment status
    }

    /**
     * Every field must be present and not null.
     * This constructor includes payment information.
     */
    public Person(Name name, StudyYear studyYear, Phone phone, Email email, Address address,
                  Set<Tag> tags, Payment payment) {
        requireAllNonNull(name, studyYear, phone, email, address, tags, payment);
        this.name = name;
        this.studyYear = studyYear;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.payment = payment;
    }

    public Name getName() {
        return name;
    }

    public StudyYear getStudyYear() {
        return studyYear;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Payment getPayment() {
        return payment;
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && (hasSameEmail(otherPerson) || hasSameNumber(otherPerson));
    }

    public boolean hasSameNumber(Person otherPerson) {
        return otherPerson.getPhone().equals(getPhone());
    }

    public boolean hasSameEmail(Person otherPerson) {
        return otherPerson.getEmail().equals(getEmail());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && studyYear.equals(otherPerson.studyYear)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && payment.equals(otherPerson.payment);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, studyYear, phone, email, address, tags, payment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("studyYear", studyYear)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("payment", payment)
                .add("billingStart", "Day " + payment.getBillingStartDay() + " of each month")
                .toString();
    }

}
