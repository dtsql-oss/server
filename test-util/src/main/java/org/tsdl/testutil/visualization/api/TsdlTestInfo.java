package org.tsdl.testutil.visualization.api;

import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.impl.TsdlTestInfoImpl;

/**
 * Represents information about a unit test for visualization purposes, in conjunction with a {@link TimeSeriesTestVisualizer} instance.
 */
public interface TsdlTestInfo {
  static TsdlTestInfo of(String shortName, String longName, List<List<DataPoint>> timeSeries) {
    return new TsdlTestInfoImpl(shortName, longName, timeSeries);
  }

  String shortName();

  String longName();

  List<List<DataPoint>> timeSeries();
}
