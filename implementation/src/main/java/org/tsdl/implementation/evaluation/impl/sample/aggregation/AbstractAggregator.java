package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.implementation.model.sample.aggregation.TsdlGlobalAggregator;
import org.tsdl.implementation.model.sample.aggregation.TsdlLocalAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Abstract base class for {@link TsdlAggregator} implementations that encapsulates common tasks such as preparing a stream of values from a list of
 * {@link DataPoint} instances as well as filtering that stream based on the interval specification of a concrete {@link TsdlLocalAggregator}.
 */
@Slf4j
public abstract class AbstractAggregator implements TsdlAggregator {
  private Double sampleValue;

  protected abstract Double aggregate(DoubleStream valueStream);

  protected abstract String descriptor();

  @Override
  public double compute(String sampleIdentifier, List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.info("Calculating '{}' {} over {} data points.", sampleIdentifier, descriptor(), dataPoints.size());

    var valueStream = getValueStream(dataPoints);
    sampleValue = aggregate(valueStream);

    log.info("Calculated '{}' {} to be {}", sampleIdentifier, descriptor(), sampleValue);

    return sampleValue;
  }

  @Override
  public double value() {
    Conditions.checkIsTrue(Condition.STATE, this::isComputed, "Sample value (%s) must have been computed before accessing it.", descriptor());
    return sampleValue;
  }

  @Override
  public boolean isComputed() {
    return sampleValue != null;
  }

  private DoubleStream getValueStream(List<DataPoint> dataPoints) {
    var relevantDataPoints = switch (this) {
      case TsdlGlobalAggregator ignored -> dataPoints.stream();
      case TsdlLocalAggregator localAggregator ->
          dataPoints.stream().filter(dp -> TsdlUtil.isWithinRange(dp.timestamp(), localAggregator.lowerBound(), localAggregator.upperBound()));
      default -> throw Conditions.exception(Condition.STATE, "Unknown type of aggregator '%s'", this.getClass().getName());
    };

    return relevantDataPoints.mapToDouble(DataPoint::value);
  }
}
