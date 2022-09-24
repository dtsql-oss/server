package org.tsdl.implementation.math;

import java.util.List;
import java.util.function.Supplier;

/**
 * Provides summary information about data with basic descriptive statistics measures.
 */
public interface SummaryStatistics {
  boolean ingest(Supplier<List<Double>> values);

  boolean hasIngested();

  double populationStandardDeviation();

  double sampleStandardDeviation();

  double minimum();

  double maximum();

  double sum();

  long count();

  double average();

  double populationVariance();

  double sampleVariance();
}
