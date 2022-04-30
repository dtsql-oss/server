package org.tsdl.infrastructure.model;

import java.util.List;

public record TsdlResult<T>(String csvFile, int column, List<DataPoint> items) {
}
