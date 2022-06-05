package org.tsdl.implementation.model.sample.aggregation;

import java.util.List;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * An aggregation operator for a {@link TsdlSample}.
 */
public interface TsdlAggregator {
  double compute(List<DataPoint> dataPoints);
}
