package org.tsdl.implementation.evaluation;


import java.util.List;
import java.util.Map;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * Provides methods to collect a final {@link QueryResult} instance based on input, samples and assembled periods.
 */
public interface TsdlResultCollector {
  /**
   * Collects a final {@link QueryResult} from the given parameters.
   *
   * @param noPeriodDefinitions flag is needed so that collector know difference between "there are event/period definitions, but no such periods were
   *                            detected (periodSet is empty)" and "there are no event/period definitions, hence periodSet is empty". this distinction
   *                            is important for when {@code result} represents {@link YieldFormat#DATA_POINTS}.
   */
  QueryResult collect(YieldStatement result, List<DataPoint> dataPoints, TsdlPeriodSet periodSet, boolean noPeriodDefinitions,
                      Map<TsdlIdentifier, Double> samples);
}
