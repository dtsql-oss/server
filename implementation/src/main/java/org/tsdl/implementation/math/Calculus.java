package org.tsdl.implementation.math;

import java.util.List;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Encapsulates methods providing discrete counterparts of concepts known from calculus.
 */
public interface Calculus {
  double definiteIntegral(List<DataPoint> dataPoints);

  List<DataPoint> derivative(List<DataPoint> dataPoints, TsdlTimeUnit differenceUnit);
}
