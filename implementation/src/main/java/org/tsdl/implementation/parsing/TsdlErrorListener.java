package org.tsdl.implementation.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class TsdlErrorListener extends BaseErrorListener {
    public static final TsdlErrorListener INSTANCE = new TsdlErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
                            RecognitionException e) {
        throw new TsdlParserException("line " + line + ":" + charPositionInLine + " " + msg);
    }
}
