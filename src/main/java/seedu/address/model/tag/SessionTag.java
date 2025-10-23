package seedu.address.model.tag;

import seedu.address.model.person.Session;

public class SessionTag extends Tag {
    private final Session session;

    public SessionTag(String tagName, Session session) {
        super(tagName);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

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
