package org.tsdl.testutil.visualization.impl;

import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.api.TsdlTestInfo;

/**
 * Default implementation of {@link TsdlTestInfo}.
 *
 * @param shortName  the short name of the test, i.e. the method name
 * @param longName   the long name of the test, i.e. the fully qualified method name
 * @param timeSeries all time series test arguments
 */
public record TsdlTestInfoImpl(String shortName, String longName, List<List<DataPoint>> timeSeries) implements TsdlTestInfo {
}
