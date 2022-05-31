package org.tsdl.testutil.visualization.impl;

import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.api.TsdlTestInfo;

import java.util.List;

public record TsdlTestInfoImpl(String shortName, String longName, List<List<DataPoint>> timeSeries) implements TsdlTestInfo {
}
