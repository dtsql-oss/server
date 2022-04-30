package org.tsdl.infrastructure.model;

import org.tsdl.infrastructure.model.impl.TsdlDataPoint;

import java.time.Instant;

public interface DataPoint {
    Instant getTimestamp();

    Object getValue();

    Long asInteger();

    Double asDecimal();

    String asText();

    static DataPoint of(Instant timestamp, Object value) {
        return new TsdlDataPoint(timestamp, value);
    }
}
