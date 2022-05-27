package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.infrastructure.model.DataPoint;

import java.util.List;

public interface TsdlAggregator {
    double compute(List<DataPoint> dataPoints);
}
