package org.tsdl.implementation.factory.impl;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.factory.TsdlElementFactory;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.TsdlErrorListener;
import org.tsdl.implementation.parsing.TsdlQueryParser;
import org.tsdl.implementation.parsing.impl.TsdlElementParserImpl;
import org.tsdl.implementation.parsing.impl.TsdlQueryParserImpl;

public class ObjectFactoryImpl implements ObjectFactory {
    @Override
    public TsdlQueryParser queryParser() {
        return new TsdlQueryParserImpl();
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
    public ANTLRErrorListener errorListener() {
        return new TsdlErrorListener();
    }
}
