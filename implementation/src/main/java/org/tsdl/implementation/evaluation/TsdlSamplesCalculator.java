package org.tsdl.implementation.evaluation;

import java.util.List;
import java.util.Map;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Provides method to calculate the values of threshold events and threshold filters.
 */
public interface TsdlSamplesCalculator {
  Map<TsdlIdentifier, Double> computeSampleValues(List<TsdlSample> samples, List<DataPoint> dataPoints, List<TsdlLogEvent> logEvents);

  /**
   * Precondition: {@link #computeSampleValues(List, List, List)} has already been executed.
   */
  void setConnectiveArgumentValues(SinglePointFilterConnective filter, List<TsdlEvent> events, Map<TsdlIdentifier, Double> sampleValues);
}
