package org.tsdl.implementation.model.sample.aggregation;

import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;

public interface TsdlAggregator {
  double compute(List<DataPoint> dataPoints);
}
