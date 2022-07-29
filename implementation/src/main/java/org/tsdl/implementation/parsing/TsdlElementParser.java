package org.tsdl.implementation.parsing;

import java.time.Instant;
import org.tsdl.implementation.model.event.EventDurationBound;
import org.tsdl.implementation.model.event.EventDurationUnit;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.TemporalFilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.enums.ThresholdFilterType;

/**
 * Provides methods for parsing elements/components of {@link org.tsdl.implementation.model.TsdlQuery}.
 */
public interface TsdlElementParser {
  ConnectiveIdentifier parseConnectiveIdentifier(String str);

  ThresholdFilterType parseThresholdFilterType(String str);

  TemporalFilterType parseTemporalFilterType(String str);

  YieldFormat parseResultFormat(String str);

  AggregatorType parseAggregatorType(String str);

  TemporalRelationType parseTemporalRelationType(String str);

  EventDurationBound parseEventDurationBound(String str, boolean lowerBound);

  EventDurationUnit parseEventDurationUnit(String str);

  Double parseNumber(String str);

  Long parseInteger(String str);

  String parseStringLiteral(String str);

  Instant parseDateLiteral(String str);
}
