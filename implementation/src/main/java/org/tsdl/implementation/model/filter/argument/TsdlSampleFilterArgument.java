package org.tsdl.implementation.model.filter.argument;

import org.tsdl.implementation.model.sample.TsdlSample;

/**
 * An argument to a TSDL filter which is a {@link TsdlSample}.
 */
public interface TsdlSampleFilterArgument extends TsdlFilterArgument {
  TsdlSample sample();

  void setValue(Double value);
}
