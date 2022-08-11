package org.tsdl.implementation.math;

import java.util.List;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Encapsulates interpolation-related functions to increase the sampling rate of a discrete signal.
 */
public interface DiscreteInterpolation {
  List<DataPoint> linear(List<DataPoint> dataPoints, int samplingRate, TsdlTimeUnit unit);

  List<DataPoint> quadratic(List<DataPoint> dataPoints, int samplingRate, TsdlTimeUnit unit);

  List<DataPoint> cubic(List<DataPoint> dataPoints, int samplingRate, TsdlTimeUnit unit);
}
