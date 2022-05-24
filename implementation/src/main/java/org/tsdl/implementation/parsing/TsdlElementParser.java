package org.tsdl.implementation.parsing;

import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;

public interface TsdlElementParser {
    ConnectiveIdentifier parseConnectiveIdentifier(String str);

    FilterType parseFilterType(String str);

    Double parseNumber(String str);
}
