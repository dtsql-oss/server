package org.tsdl.implementation.model.connective;

import java.util.List;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * A filter connective where the evaluation for a data point depends on a single data point.
 */
public interface SinglePointFilterConnective {

  List<SinglePointFilter> filters();

  /**
   * Postcondition: order of input data has been preserved; merely non-matching items have been filtered out.
   */
  List<DataPoint> evaluateFilters(List<DataPoint> data);

  boolean isSatisfied(DataPoint dp);
}
