package org.vitaliistf.sessions;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> attributes;

    public Session() {
        attributes = new HashMap<>();
    }

    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    public void setAttribute(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    public void removeAttribute(String attributeName) {
        attributes.remove(attributeName);
    }

    public void clearAttributes() {
        attributes.clear();
    }

    public Object getAndRemoveAttribute(String attributeName) {
        Object temp = attributes.get(attributeName);
        attributes.remove(attributeName);
        return temp;
    }
}

