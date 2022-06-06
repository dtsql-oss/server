package org.tsdl.infrastructure.model.impl;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlDataPoints;

/**
 * Default implementation of the {@link TsdlDataPoints} interface.
 *
 * @param items {@link DataPoint} instances making up the query result
 */
public record TsdlDataPointsImpl(List<DataPoint> items) implements TsdlDataPoints {
  public TsdlDataPointsImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, items, "Items must not be null.");
  }
}
