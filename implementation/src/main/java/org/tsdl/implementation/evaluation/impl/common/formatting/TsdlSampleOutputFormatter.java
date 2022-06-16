package org.tsdl.implementation.evaluation.impl.common.formatting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.model.common.TsdlOutputFormatter;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Concrete output formatter for {@link TsdlSample} instances.
 */
public class TsdlSampleOutputFormatter implements TsdlOutputFormatter<TsdlSample> {
  private static final TsdlElementParser elementParser = ObjectFactory.INSTANCE.elementParser();
  private final int decimalPlaces;
  private final String[] args;

  /**
   * Initializes the {@link TsdlSampleOutputFormatter}.
   *
   * @param args a string array of argument, expected: exactly one argument representing a non-negative integer
   */
  public TsdlSampleOutputFormatter(String... args) {
    Conditions.checkNotNull(Condition.ARGUMENT, args, "Output formatting arguments must not be null.");
    Conditions.checkSizeExactly(Condition.ARGUMENT, args, 1, "There must be exactly one argument to the sample output formatter.");

    var decimalArgument = elementParser.parseInteger(args[0]);
    Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, decimalArgument, 0, "Number of decimal places must be greater than or equal to 0.");

    this.args = args;
    decimalPlaces = decimalArgument;
  }

  @Override
  public String format(TsdlSample obj) {
    Conditions.checkNotNull(Condition.ARGUMENT, obj, "Sample to format must not be null.");

    var format = "0" + (decimalPlaces > 0 ? ".%s".formatted("0".repeat(decimalPlaces)) : "");
    var decimalFormat = new DecimalFormat(format);
    var symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setDecimalSeparator('.');
    decimalFormat.setDecimalFormatSymbols(symbols);
    decimalFormat.setGroupingUsed(false);

    var sampleName = obj.identifier().name();
    var aggregatorFunction = obj.aggregator().type().representation();
    var value = obj.aggregator().value();

    return "sample '%s' of '%s' aggregator := %s".formatted(sampleName, aggregatorFunction, decimalFormat.format(value));
  }

  @Override
  public String[] args() {
    return args;
  }
}
