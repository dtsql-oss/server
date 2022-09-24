package org.tsdl.implementation.evaluation.impl.filter.argument;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.tsdl.implementation.model.filter.argument.TsdlSampleScalarArgument;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlSampleScalarArgument}.
 */
@EqualsAndHashCode
@ToString
public class TsdlSampleScalarArgumentImpl implements TsdlSampleScalarArgument {
  private final TsdlSample sample;
  private double value;

  public TsdlSampleScalarArgumentImpl(TsdlSample sample, double value) {
    Conditions.checkNotNull(Condition.ARGUMENT, sample, "Sample of sample filter argument must not be null.");
    this.sample = sample;
    this.value = value;
  }

  public TsdlSampleScalarArgumentImpl(TsdlSample sample) {
    this(sample, Double.NaN);
  }

  @Override
  public double value() {
    Conditions.checkIsFalse(Condition.STATE, Double.isNaN(value), "Argument value has not been set yet.");
    return value;
  }

  @Override
  public void setValue(double value) {
    Conditions.checkIsFalse(Condition.ARGUMENT, Double.isNaN(value), "NaN is not a valid argument value.");
    this.value = value;
  }

  @Override
  public TsdlSample sample() {
    return sample;
  }
}
