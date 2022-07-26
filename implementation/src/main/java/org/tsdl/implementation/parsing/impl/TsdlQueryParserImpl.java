package org.tsdl.implementation.parsing.impl;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.tsdl.grammar.TsdlLexer;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.parsing.TsdlQueryParser;
import org.tsdl.implementation.parsing.exception.TsdlParseException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlQuery}.
 */
public class TsdlQueryParserImpl implements TsdlQueryParser {
  private final ANTLRErrorListener errorListener = TsdlComponentFactory.INSTANCE.errorListener();

  @Override
  public TsdlQuery parseQuery(String query) {
    try {
      Conditions.checkNotNull(Condition.ARGUMENT, query, "Query to parse must not be null.");

      var lexer = new TsdlLexer(CharStreams.fromString(query));
      lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
      lexer.addErrorListener(errorListener);

      var tokens = new CommonTokenStream(lexer);
      var parser = new org.tsdl.grammar.TsdlParser(tokens);
      parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
      parser.addErrorListener(errorListener);

      var walker = new ParseTreeWalker();
      var tsdlListener = new TsdlListenerImpl();
      walker.walk(tsdlListener, parser.tsdlQuery());

      return tsdlListener.getQuery();
    } catch (TsdlParseException e) {
      if (e.getClass().equals(TsdlParseException.class)) {
        throw e;
      } else {
        throw new TsdlParseException("Parsing query string failed.", e);
      }
    } catch (Exception e) {
      throw new TsdlParseException("Parsing query string failed.", e);
    }
  }
}
