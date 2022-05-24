package org.tsdl.infrastructure.extension;

import org.tsdl.infrastructure.model.DataPoint;

import java.util.List;

public record TsdlTestInfo(String shortName, String longName, List<List<DataPoint>> timeSeries) {
}
