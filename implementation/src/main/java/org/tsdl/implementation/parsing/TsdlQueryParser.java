package org.tsdl.implementation.parsing;

import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.parsing.exception.TsdlParserException;

/**
 * Represents a parser for TSDL queries.
 */
public interface TsdlQueryParser {
  TsdlQuery parseQuery(String query) throws TsdlParserException;
}
