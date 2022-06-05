package org.tsdl.testutil.visualization.api;

import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.impl.TsdlTestInfoImpl;

public interface TsdlTestInfo {
  static TsdlTestInfo of(String shortName, String longName, List<List<DataPoint>> timeSeries) {
    return new TsdlTestInfoImpl(shortName, longName, timeSeries);
  }

  String shortName();

  String longName();

  List<List<DataPoint>> timeSeries();
}
