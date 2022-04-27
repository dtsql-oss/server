package org.tsdl.implementation.model;

public enum TsdlOperator {
    GT, LT;

    public static TsdlOperator fromString(String op) {
        if ("gt".equals(op)) {
            return GT;
        } else if ("lt".equals(op)) {
            return LT;
        } else {
            throw new IllegalArgumentException("There is no known query operator for the string '%s'".formatted(op));
        }
    }
}
