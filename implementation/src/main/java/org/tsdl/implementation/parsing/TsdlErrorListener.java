package org.tsdl.implementation.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.tsdl.implementation.parsing.exception.TsdlParserException;

/**
 * A custom error listener for ANTLR-generated parsing errors.
 */
public class TsdlErrorListener extends BaseErrorListener {
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
                          RecognitionException e) {
    throw new TsdlParserException("line " + line + ", position " + charPositionInLine + ": " + msg);
  }
}
