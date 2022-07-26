package org.tsdl.implementation.evaluation;


import java.util.List;
import java.util.Map;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * Provides methods to collect a final {@link QueryResult} instance based on input, samples and assembled periods.
 */
public interface TsdlResultCollector {
  QueryResult collect(YieldStatement result, TsdlPeriodSet periods, List<DataPoint> dataPoints, Map<TsdlIdentifier, Double> samples);

  QueryResult collect(YieldStatement result, List<DataPoint> dataPoints, Map<TsdlIdentifier, Double> samples);
}
