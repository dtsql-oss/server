package org.tsdl.implementation.factory;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.tsdl.implementation.factory.impl.ObjectFactoryImpl;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.TsdlParser;

public interface ObjectFactory {
    ObjectFactory INSTANCE = new ObjectFactoryImpl();

    TsdlParser getParser();

    TsdlElementParser elementParser();

    TsdlElementFactory elementFactory();

    ANTLRErrorListener getErrorListener();
}
