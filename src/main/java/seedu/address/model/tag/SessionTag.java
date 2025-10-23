package seedu.address.model.tag;

import seedu.address.model.person.Session;

/**
 * Represents a session tag in the address book.
 * A SessionTag is a specialized tag that contains both a tag name (formatted session string)
 * and the underlying Session object with temporal information.
 * This allows session-specific operations and differentiation from regular tags.
 * Guarantees: immutable; tag name and session are valid as declared in {@link Tag} and {@link Session}.
 */
public class SessionTag extends Tag {
    private final Session session;

    /**
     * Constructs a SessionTag.
     *
     * @param tagName A valid tag name representing the session (e.g., "MON 0900 - 1000").
     * @param session The Session object containing the temporal information.
     */
    public SessionTag(String tagName, Session session) {
        super(tagName);
        this.session = session;
    }

    /**
     * Returns the Session object associated with this SessionTag.
     *
     * @return The Session object.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Returns true to indicate this is a session tag.
     *
     * @return true.
     */
    @Override
    public boolean isSessionTag() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SessionTag)) {
            return false;
        }

        SessionTag otherTag = (SessionTag) other;
        return tagName.equals(otherTag.tagName) && session.equals(otherTag.getSession());
    }

    @Override
    public int hashCode() {
        return tagName.hashCode() + session.hashCode();
    }
}
