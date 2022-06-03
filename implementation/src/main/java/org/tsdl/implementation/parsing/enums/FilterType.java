package org.tsdl.implementation.parsing.enums;

import org.tsdl.implementation.model.common.Identifiable;

public enum FilterType implements Identifiable {
    GT("gt"), LT("lt");

    private final String representation;

    FilterType(String representation) {
        this.representation = representation;
    }

    public String representation() {
        return representation;
    }
}
