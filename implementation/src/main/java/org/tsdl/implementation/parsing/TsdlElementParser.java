package org.tsdl.implementation.parsing;

import org.tsdl.implementation.model.result.ResultFormat;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;

public interface TsdlElementParser {
  ConnectiveIdentifier parseConnectiveIdentifier(String str);

  FilterType parseFilterType(String str);

  ResultFormat parseResultFormat(String str);

  AggregatorType parseAggregatorType(String str);

  TemporalRelationType parseTemporalRelationType(String str);

  Double parseNumber(String str);
}
