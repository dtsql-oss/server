package org.tsdl.implementation.parsing;

import org.tsdl.implementation.model.TsdlQuery;

public interface TsdlQueryParser {
    TsdlQuery parseQuery(String query) throws TsdlParserException;
}
