package org.tsdl.implementation.model.filter.argument;

import org.tsdl.implementation.model.sample.TsdlSample;

/**
 * A scalar argument which is a {@link TsdlSample}.
 */
public interface TsdlSampleScalarArgument extends TsdlScalarArgument {
  TsdlSample sample();

  void setValue(double value);
}
