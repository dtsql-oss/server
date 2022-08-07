package org.tsdl.implementation.parsing;

import java.time.Instant;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.common.TsdlDurationBound;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.DeviationFilterType;
import org.tsdl.implementation.parsing.enums.TemporalFilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.enums.ThresholdFilterType;

/**
 * Provides methods for parsing elements/components of {@link org.tsdl.implementation.model.TsdlQuery}.
 */
public interface TsdlElementParser {
  /**
   * Represents different kinds of duration bounds.
   */
  enum DurationBoundType {
    LOWER_BOUND, UPPER_BOUND
  }

  ConnectiveIdentifier parseConnectiveIdentifier(String str);

  ThresholdFilterType parseThresholdFilterType(String str);

  DeviationFilterType parseDeviationFilterType(String str, String type);

  TemporalFilterType parseTemporalFilterType(String str);

  YieldFormat parseResultFormat(String str);

  AggregatorType parseAggregatorType(String str);

  TemporalRelationType parseTemporalRelationType(String str);

  TsdlDurationBound parseEventDurationBound(String str, DurationBoundType boundType);

  ParsableTsdlTimeUnit parseEventDurationUnit(String str);

  TimePeriod parseTimePeriod(String str);

  double parseNumber(String str);

  long parseInteger(String str);

  String parseStringLiteral(String str);

  Instant parseDate(String str, boolean literal);
}
