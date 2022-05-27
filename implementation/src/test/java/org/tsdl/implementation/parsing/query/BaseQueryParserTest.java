package org.tsdl.implementation.parsing.query;

import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.factory.TsdlElementFactory;
import org.tsdl.implementation.parsing.TsdlQueryParser;

abstract class BaseQueryParserTest {
    protected final TsdlQueryParser parser = ObjectFactory.INSTANCE.queryParser();

    protected final TsdlElementFactory element = ObjectFactory.INSTANCE.elementFactory();
}
