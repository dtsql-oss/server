package org.tsdl.implementation.parsing.enums;

public enum ConnectiveIdentifier {
    AND("AND"), OR("OR");

    private final String representation;

    ConnectiveIdentifier(String representation) {
        this.representation = representation;
    }

    public String representation() {
        return representation;
    }
}
