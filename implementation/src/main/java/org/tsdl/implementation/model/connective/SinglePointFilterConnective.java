package org.tsdl.implementation.model.connective;

import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

import java.util.List;

public interface SinglePointFilterConnective {

    List<SinglePointFilter> filters();

    List<DataPoint> evaluateFilters(List<DataPoint> data);
}
