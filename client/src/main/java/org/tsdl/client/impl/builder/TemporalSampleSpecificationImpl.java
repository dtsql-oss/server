package org.tsdl.client.impl.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.tsdl.client.api.builder.EchoSpecification;
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
  private final EchoSpecification echo;
  private final TemporalSampleType type;

  private TemporalSampleSpecificationImpl(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods, EchoSpecification echo,
                                          TemporalSampleType type) {
    if (identifier == null || identifier.trim().isEmpty()) {
      throw new TsdlQueryBuildException("Sample identifier must neither be null nor blank.");
    }
    if (periods == null || periods.isEmpty()) {
      throw new TsdlQueryBuildException("Periods to aggregate over must neither be null nor empty.");
    }

    this.identifier = identifier;
    this.unit = unit;
    this.periods = periods;
    this.echo = echo;
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
  public Optional<EchoSpecification> echo() {
    return Optional.ofNullable(echo);
  }

  @Override
  public TemporalSampleType type() {
    return type;
  }

  public static TemporalSampleSpecification averageTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, echo, TemporalSampleType.AVERAGE);
  }

  public static TemporalSampleSpecification averageTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, null, TemporalSampleType.AVERAGE);
  }

  public static TemporalSampleSpecification averageTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return averageTemporal(identifier, unit, Arrays.stream(periods).toList());
  }

  public static TemporalSampleSpecification averageTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo, QueryPeriod... periods) {
    return averageTemporal(identifier, unit, echo, Arrays.stream(periods).toList());
  }


  public static TemporalSampleSpecification maximumTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, echo, TemporalSampleType.MAXIMUM);
  }

  public static TemporalSampleSpecification maximumTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return maximumTemporal(identifier, unit, null, periods);
  }

  public static TemporalSampleSpecification maximumTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo, QueryPeriod... periods) {
    return maximumTemporal(identifier, unit, echo, Arrays.stream(periods).toList());
  }

  public static TemporalSampleSpecification maximumTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return maximumTemporal(identifier, unit, Arrays.stream(periods).toList());
  }


  public static TemporalSampleSpecification minimumTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, echo, TemporalSampleType.MINIMUM);
  }

  public static TemporalSampleSpecification minimumTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return minimumTemporal(identifier, unit, null, periods);
  }

  public static TemporalSampleSpecification minimumTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo, QueryPeriod... periods) {
    return minimumTemporal(identifier, unit, echo, Arrays.stream(periods).toList());
  }

  public static TemporalSampleSpecification minimumTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return minimumTemporal(identifier, unit, null, Arrays.stream(periods).toList());
  }


  public static TemporalSampleSpecification sumTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, echo, TemporalSampleType.SUM);
  }

  public static TemporalSampleSpecification sumTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return sumTemporal(identifier, unit, null, periods);
  }

  public static TemporalSampleSpecification sumTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo, QueryPeriod... periods) {
    return sumTemporal(identifier, unit, echo, Arrays.stream(periods).toList());
  }

  public static TemporalSampleSpecification sumTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return sumTemporal(identifier, unit, Arrays.stream(periods).toList());
  }


  public static TemporalSampleSpecification standardDeviationTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo,
                                                                      List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, unit, periods, echo, TemporalSampleType.STANDARD_DEVIATION);
  }

  public static TemporalSampleSpecification standardDeviationTemporal(String identifier, TsdlTimeUnit unit, List<QueryPeriod> periods) {
    return standardDeviationTemporal(identifier, unit, null, periods);
  }

  public static TemporalSampleSpecification standardDeviationTemporal(String identifier, TsdlTimeUnit unit, EchoSpecification echo,
                                                                      QueryPeriod... periods) {
    return standardDeviationTemporal(identifier, unit, echo, Arrays.stream(periods).toList());
  }

  public static TemporalSampleSpecification standardDeviationTemporal(String identifier, TsdlTimeUnit unit, QueryPeriod... periods) {
    return standardDeviationTemporal(identifier, unit, null, periods);
  }


  public static TemporalSampleSpecification countTemporal(String identifier, EchoSpecification echo, List<QueryPeriod> periods) {
    return new TemporalSampleSpecificationImpl(identifier, null, periods, echo, TemporalSampleType.COUNT);
  }

  public static TemporalSampleSpecification countTemporal(String identifier, List<QueryPeriod> periods) {
    return countTemporal(identifier, null, periods);
  }

  public static TemporalSampleSpecification countTemporal(String identifier, EchoSpecification echo, QueryPeriod... periods) {
    return countTemporal(identifier, echo, Arrays.stream(periods).toList());
  }

  public static TemporalSampleSpecification countTemporal(String identifier, QueryPeriod... periods) {
    return countTemporal(identifier, null, Arrays.stream(periods).toList());
  }
}
