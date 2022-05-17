package org.tsdl.implementation.factory.impl;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.factory.TsdlElementFactory;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.TsdlErrorListener;
import org.tsdl.implementation.parsing.TsdlParser;
import org.tsdl.implementation.parsing.impl.TsdlElementParserImpl;
import org.tsdl.implementation.parsing.impl.TsdlParserImpl;

public class ObjectFactoryImpl implements ObjectFactory {
    static ObjectFactory INSTANCE = new ObjectFactoryImpl();

    @Override
    public TsdlParser getParser() {
        return new TsdlParserImpl();
    }

    @Override
    public TsdlElementParser elementParser() {
        return new TsdlElementParserImpl();
    }

    @Override
    public TsdlElementFactory elementFactory() {
        return new TsdlElementFactoryImpl();
    }

    @Override
    public ANTLRErrorListener getErrorListener() {
        return new TsdlErrorListener();
    }
}
