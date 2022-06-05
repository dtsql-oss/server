package org.tsdl.implementation.model.filter.argument;

import org.tsdl.implementation.model.sample.TsdlSample;

public interface TsdlSampleFilterArgument extends TsdlFilterArgument {
  TsdlSample sample();

  void setValue(Double value);
}
