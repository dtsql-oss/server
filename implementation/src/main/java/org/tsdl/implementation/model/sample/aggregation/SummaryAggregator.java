package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.math.SummaryStatistics;

/**
 * An aggregator that is used to compute basic statistic summary values.
 */
public interface SummaryAggregator extends TsdlAggregator {
  void setStatistics(SummaryStatistics statistics);
}
