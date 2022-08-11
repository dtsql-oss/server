package org.tsdl.implementation.math.impl;

import java.util.List;
import java.util.function.Supplier;
import org.tsdl.implementation.math.SummaryStatistics;

/**
 * Default implementation of {@link SummaryStatistics}. Calculates standard deviation using Welford's online algorithm and sum using
 * Neumaier's variant of Kahan summation.
 */
public class SummaryStatisticsImpl implements SummaryStatistics {
  private double min = Double.POSITIVE_INFINITY;
  private double max = Double.NEGATIVE_INFINITY;

  private double naiveSum = 0.0;
  private double neumaierSum = 0.0;
  private double compensation = 0.0;
  private double m2 = 0.0; // M_(2,n) = M_(2,n-1) + (x_n-avg_(n-1))*(x_n - avg_n) [sum of product of value's delta from previous and current avg]

  private long count = 0L;
  private double average = 0.0;

  private boolean hasIngested = false;

  @Override
  public boolean ingest(Supplier<List<Double>> valueSupplier) {
    if (hasIngested) {
      return false;
    }

    hasIngested = true;
    valueSupplier.get().forEach(this::ingest);

    return true;
  }

  private void ingest(double value) {
    min = Math.min(min, value);
    max = Math.max(max, value);

    ingestNeumaierSum(value);
    naiveSum += value;

    count++;
    var deltaFromPreviousAvg = value - average;
    average += deltaFromPreviousAvg / count;
    var deltaFromCurrentAvg = value - average;
    m2 += deltaFromPreviousAvg * deltaFromCurrentAvg;
  }

  private void ingestNeumaierSum(double value) {
    var tmp = neumaierSum + value;
    compensation += (Math.abs(neumaierSum) >= Math.abs(value)) ? (neumaierSum - tmp) + value : (value - tmp) + neumaierSum;
    neumaierSum = tmp;
  }


  @Override
  public boolean hasIngested() {
    return hasIngested;
  }

  @Override
  public long count() {
    return count;
  }


  @Override
  public double average() {
    return average;
  }


  @Override
  public double populationVariance() {
    return count > 1 ? m2 / count : 0.0;
  }


  @Override
  public double sampleVariance() {
    return count > 1 ? m2 / (count - 1) : 0.0;
  }


  @Override
  public double populationStandardDeviation() {
    return Math.sqrt(populationVariance());
  }

  @Override
  public double sampleStandardDeviation() {
    return Math.sqrt(sampleVariance());
  }

  @Override
  public double minimum() {
    return min;
  }

  @Override
  public double maximum() {
    return max;
  }

  @Override
  public double sum() {
    neumaierSum += compensation;
    if (Double.isNaN(neumaierSum) && Double.isInfinite(naiveSum)) {
      neumaierSum = naiveSum;
    }
    return neumaierSum;
  }
}
