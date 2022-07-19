package org.tsdl.implementation.parsing;

import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;

/**
 * Provides methods for parsing elements/components of {@link org.tsdl.implementation.model.TsdlQuery}.
 */
public interface TsdlElementParser {
  ConnectiveIdentifier parseConnectiveIdentifier(String str);

  FilterType parseFilterType(String str);

  YieldFormat parseResultFormat(String str);

  AggregatorType parseAggregatorType(String str);

  TemporalRelationType parseTemporalRelationType(String str);

  Double parseNumber(String str);

  Integer parseInteger(String str);

  String parseStringLiteral(String str);
}
