package org.tsdl.implementation.evaluation;

import java.util.List;
import java.util.Map;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Provides method to calculate the values of threshold events and threshold filters.
 */
public interface TsdlSamplesCalculator {
  Map<TsdlIdentifier, Double> computeSampleValues(List<TsdlSample> samples, List<DataPoint> dataPoints, List<TsdlLogEvent> logEvents);

  void setConnectiveArgumentValues(TsdlQuery query, Map<TsdlIdentifier, Double> sampleValues);
}
