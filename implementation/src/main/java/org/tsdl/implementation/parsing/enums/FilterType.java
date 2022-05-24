package org.tsdl.implementation.parsing.enums;

public enum FilterType {
    GT("gt"), LT("lt");

    private final String representation;

    FilterType(String representation) {
        this.representation = representation;
    }

    public String representation() {
        return representation;
    }
}
