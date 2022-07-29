package org.tsdl.implementation.factory.impl;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.tsdl.implementation.evaluation.TsdlResultCollector;
import org.tsdl.implementation.evaluation.TsdlSamplesCalculator;
import org.tsdl.implementation.evaluation.impl.TsdlPeriodAssembler;
import org.tsdl.implementation.evaluation.impl.TsdlPeriodAssemblerImpl;
import org.tsdl.implementation.evaluation.impl.TsdlSamplesCalculatorImpl;
import org.tsdl.implementation.evaluation.impl.result.TsdlResultCollectorImpl;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.TsdlErrorListener;
import org.tsdl.implementation.parsing.TsdlQueryParser;
import org.tsdl.implementation.parsing.impl.TsdlElementParserImpl;
import org.tsdl.implementation.parsing.impl.TsdlQueryParserImpl;

/**
 * Default implementation of {@link TsdlComponentFactory}.
 */
public class TsdlComponentFactoryImpl implements TsdlComponentFactory {
  @Override
  public TsdlQueryParser queryParser() {
    return new TsdlQueryParserImpl();
  }

  @Override
  public TsdlElementParser elementParser() {
    return new TsdlElementParserImpl();
  }

  @Override
  public TsdlResultCollector resultCollector() {
    return new TsdlResultCollectorImpl();
  }

  @Override
  public TsdlSamplesCalculator samplesCalculator() {
    return new TsdlSamplesCalculatorImpl();
  }

  @Override
  public TsdlPeriodAssembler periodAssembler() {
    return new TsdlPeriodAssemblerImpl();
  }

  @Override
  public TsdlQueryElementFactory elementFactory() {
    return new TsdlQueryElementFactoryImpl();
  }

  @Override
  public ANTLRErrorListener errorListener() {
    return new TsdlErrorListener();
  }
}
