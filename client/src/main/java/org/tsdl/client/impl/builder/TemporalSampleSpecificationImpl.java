package org.tsdl.client.impl.builder;

import java.util.Arrays;
import java.util.List;
import org.tsdl.client.api.builder.QueryPeriod;
import org.tsdl.client.api.builder.TemporalSampleSpecification;
import org.tsdl.client.util.TsdlQueryBuildException;
import org.tsdl.infrastructure.common.TsdlTimeUnit;

/**
 * Default implementation of {@link TemporalSampleSpecification}.
 */
public final class TemporalSampleSpecificationImpl implements TemporalSampleSpecification {
  private final String identifier;
  private final TsdlTimeUnit unit;
  private final List<QueryPeriod> periods;
  private final TemporalSampleType type;

  private TemporalSampleSpecificationImpl(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods, TemporalSampleType type) {
    if (identifier == null || identifier.trim().isEmpty()) {
      throw new TsdlQueryBuildException("Sample identifier must neither be null nor blank.");
    }
    if (periods == null || periods.isEmpty()) {
      throw new TsdlQueryBuildException("Periods to aggregate over must neither be null nor empty.");
    }

    this.identifier = identifier;
    this.unit = unit;
    this.periods = periods;
    this.type = type;
  }

  @Override
  public String identifier() {
    return identifier;
  }

  @Override
  public TsdlTimeUnit unit() {
    return unit;
  }

  @Override
  public List<QueryPeriod> periods() {
    return periods;
  }

  @Override
  public TemporalSampleType type() {
    return type;
  }

  public static TemporalSampleSpecification averageTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, TemporalSampleType.AVERAGE);
  }

  public static TemporalSampleSpecification averageTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, Arrays.stream(periods).toList(), TemporalSampleType.AVERAGE);
  }

  public static TemporalSampleSpecification maximumTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, TemporalSampleType.MAXIMUM);
  }

  public static TemporalSampleSpecification maximumTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, Arrays.stream(periods).toList(), TemporalSampleType.MAXIMUM);
  }

  public static TemporalSampleSpecification minimumTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, TemporalSampleType.MINIMUM);
  }

  public static TemporalSampleSpecification minimumTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, Arrays.stream(periods).toList(), TemporalSampleType.MINIMUM);
  }

  public static TemporalSampleSpecification sumTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, TemporalSampleType.SUM);
  }

  public static TemporalSampleSpecification sumTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, Arrays.stream(periods).toList(), TemporalSampleType.SUM);
  }

  public static TemporalSampleSpecification standardDeviationTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, TemporalSampleType.STANDARD_DEVIATION);
  }

  public static TemporalSampleSpecification standardDeviationTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, Arrays.stream(periods).toList(), TemporalSampleType.STANDARD_DEVIATION);
  }

  public static TemporalSampleSpecification countTemporal(String identifier, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, null, periods, TemporalSampleType.COUNT);
  }

  public static TemporalSampleSpecification countTemporal(String identifier, QueryPeriod... periods) {
    return new TemporalSampleSpecificationImpl(identifier, null, Arrays.stream(periods).toList(), TemporalSampleType.COUNT);
  }
}
