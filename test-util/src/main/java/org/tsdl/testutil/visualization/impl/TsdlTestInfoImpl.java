package org.tsdl.testutil.visualization.impl;

import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.api.TsdlTestInfo;

public record TsdlTestInfoImpl(String shortName, String longName, List<List<DataPoint>> timeSeries) implements TsdlTestInfo {
}
