package org.tsdl.implementation.parsing;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.tsdl.grammar.TsdlLexer;
import org.tsdl.implementation.model.TsdlQuery;

public class TsdlParser {
    private TsdlParser() {
    }

    public static final TsdlParser INSTANCE = new TsdlParser();

    public TsdlQuery parseQuery(String query) {
        var lexer = new TsdlLexer(CharStreams.fromString(query));
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        lexer.addErrorListener(TsdlErrorListener.INSTANCE);

        var tokens = new CommonTokenStream(lexer);
        var parser = new org.tsdl.grammar.TsdlParser(tokens);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(TsdlErrorListener.INSTANCE);

        var walker = new ParseTreeWalker();
        var tsdlWalker = new TsdlListener();
        walker.walk(tsdlWalker, parser.tsdl());

        return tsdlWalker.getQuery();
    }
}
