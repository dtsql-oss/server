package org.tsdl.testutil.visualization.impl;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.api.TsdlTestInfo;

/**
 * Default implementation of {@link TsdlTestInfo}.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class TsdlTestInfoImpl implements TsdlTestInfo {
  private final String shortName;
  private final String longName;
  private final List<List<DataPoint>> timeSeries;

  /**
   * Initializes a {@link TsdlTestInfoImpl} instance.
   */
  public TsdlTestInfoImpl(String shortName, String longName, List<List<DataPoint>> timeSeries) {
    Conditions.checkNotNull(Condition.ARGUMENT, shortName, "Short name of test info must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, longName, "Long name of test info must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, timeSeries, "List of time series of test info must not be null.");
    this.shortName = shortName;
    this.longName = longName;
    this.timeSeries = timeSeries;
  }
}
