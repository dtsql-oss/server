package org.tsdl.implementation.model.result;

import org.tsdl.implementation.model.common.Identifiable;

public enum ResultFormat implements Identifiable {
    ALL_PERIODS("all periods"),
    LONGEST_PERIOD("longest period"),
    SHORTEST_PERIOD("shortest period"),
    DATA_POINTS("data points");

    private final String representation;

    ResultFormat(String representation) {
        this.representation = representation;
    }

    public String representation() {
        return representation;
    }
}
