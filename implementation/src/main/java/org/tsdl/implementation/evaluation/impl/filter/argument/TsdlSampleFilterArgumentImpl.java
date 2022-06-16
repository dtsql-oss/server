package org.tsdl.implementation.evaluation.impl.filter.argument;

import java.util.Objects;
import org.tsdl.implementation.model.filter.argument.TsdlSampleFilterArgument;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlSampleFilterArgument}.
 */
public class TsdlSampleFilterArgumentImpl implements TsdlSampleFilterArgument {
  private final TsdlSample sample;
  private Double value;

  public TsdlSampleFilterArgumentImpl(TsdlSample sample, Double value) {
    Conditions.checkNotNull(Condition.ARGUMENT, sample, "Sample of sample filter argument must not be null.");
    this.sample = sample;
    this.value = value;
  }

  public TsdlSampleFilterArgumentImpl(TsdlSample sample) {
    this(sample, null);
  }

  @Override
  public Double value() {
    Conditions.checkNotNull(Condition.STATE, value, "Argument value has not been set yet.");
    return value;
  }

  @Override
  public void setValue(Double value) {
    this.value = value;
  }

  @Override
  public TsdlSample sample() {
    return sample;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var that = (TsdlSampleFilterArgumentImpl) o;
    return Objects.equals(value, that.value) && Objects.equals(sample, that.sample);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, sample);
  }
}
