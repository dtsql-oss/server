package org.tsdl.implementation.parsing;

import org.tsdl.implementation.model.TsdlQuery;

public interface TsdlParser {
    TsdlQuery parseQuery(String query) throws TsdlParserException;
}
