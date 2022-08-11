package org.tsdl.implementation.factory;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.tsdl.implementation.evaluation.TsdlResultCollector;
import org.tsdl.implementation.evaluation.TsdlSamplesCalculator;
import org.tsdl.implementation.evaluation.impl.TsdlPeriodAssembler;
import org.tsdl.implementation.factory.impl.TsdlComponentFactoryImpl;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.TsdlQueryParser;

/**
 * A factory for instantiating key elements.
 */
public interface TsdlComponentFactory {
  TsdlComponentFactory INSTANCE = new TsdlComponentFactoryImpl();

  TsdlQueryParser queryParser();

  TsdlElementParser elementParser();

  TsdlResultCollector resultCollector();

  TsdlSamplesCalculator samplesCalculator();

  TsdlPeriodAssembler periodAssembler();

  TsdlQueryElementFactory elementFactory();

  SummaryStatistics summaryStatistics();

  Calculus calculus();

  ANTLRErrorListener errorListener();
}
