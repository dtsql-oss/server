package org.tsdl.infrastructure.model;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.time.Instant;

public record DataPoint(Instant timestamp, Object value) {
    public DataPoint {
        Conditions.checkNotNull(Condition.ARGUMENT, timestamp, "Timestamp must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
    }

    public Long asInteger() {
        return Long.valueOf(value.toString());
    }

    public Double asDecimal() {
        return Double.valueOf(value.toString());
    }

    public String asText() {
        return value.toString();
    }
}
