package org.tsdl.implementation.model.sample;

import java.io.IOException;
import java.util.List;
import org.tsdl.implementation.model.common.TsdlFormattable;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * A sample representing a special value in a TSDL query.
 */
public interface TsdlSample extends TsdlFormattable<TsdlSample> {
  TsdlAggregator aggregator();

  TsdlIdentifier identifier();

  /**
   * Default implementation of shorthand method for delegating the sample computing to the actual {@link TsdlAggregator} represented by
   * {@link TsdlSample#aggregator()}. It also takes care of echoing this instance if {@link TsdlSample#formatter()} is present.
   *
   * @param dataPoints data points as aggregator input
   * @return computed sample value
   */
  default double compute(List<DataPoint> dataPoints) {
    var sample = aggregator().compute(dataPoints);

    if (formatter().isPresent()) {
      try {
        echo();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return sample;
  }
}
