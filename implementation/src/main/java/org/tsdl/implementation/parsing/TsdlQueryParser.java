package org.tsdl.implementation.parsing;

import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.parsing.exception.TsdlParserException;

public interface TsdlQueryParser {
    TsdlQuery parseQuery(String query) throws TsdlParserException;
}
