package org.tsdl.infrastructure.model;

import java.time.Instant;

public record DataPoint<T>(Instant timestamp, T value) {
}
