package org.tsdl.implementation.model.connective;

import java.util.List;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

public interface SinglePointFilterConnective {

  List<SinglePointFilter> filters();

  List<DataPoint> evaluateFilters(List<DataPoint> data);
}
