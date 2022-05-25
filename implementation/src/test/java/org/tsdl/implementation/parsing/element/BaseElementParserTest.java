package org.tsdl.implementation.parsing.element;

import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.parsing.TsdlElementParser;

abstract class BaseElementParserTest {
    protected final TsdlElementParser parser = ObjectFactory.INSTANCE.elementParser();
}
