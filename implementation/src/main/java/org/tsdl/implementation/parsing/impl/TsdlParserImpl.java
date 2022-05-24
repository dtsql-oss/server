package org.tsdl.implementation.parsing.impl;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.tsdl.grammar.TsdlLexer;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.parsing.TsdlParser;
import org.tsdl.implementation.parsing.TsdlParserException;

public class TsdlParserImpl implements TsdlParser {
    private final ANTLRErrorListener errorListener = ObjectFactory.INSTANCE.getErrorListener();

    @Override
    public TsdlQuery parseQuery(String query) {
        try {
            var lexer = new TsdlLexer(CharStreams.fromString(query));
            lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
            lexer.addErrorListener(errorListener);

            var tokens = new CommonTokenStream(lexer);
            var parser = new org.tsdl.grammar.TsdlParser(tokens);
            parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
            parser.addErrorListener(errorListener);

            var walker = new ParseTreeWalker();
            var tsdlListener = new TsdlListenerImpl();
            walker.walk(tsdlListener, parser.tsdl());

            return tsdlListener.getQuery();
        } catch (TsdlParserException e) {
            throw e;
        } catch (Exception e) {
            throw new TsdlParserException("Parsing query string failed.", e);
        }
    }
}