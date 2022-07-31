package org.tsdl.implementation.evaluation.impl.common.formatting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.model.common.TsdlOutputFormatter;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Concrete output formatter for {@link TsdlSample} instances.
 */
@Slf4j
public class TsdlSampleOutputFormatter implements TsdlOutputFormatter<TsdlSample> {
  private static final TsdlElementParser ELEMENT_PARSER = TsdlComponentFactory.INSTANCE.elementParser();
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

    var decimalArgument = ELEMENT_PARSER.parseInteger(args[0]);
    Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, decimalArgument, 0L, "Number of decimal places must be greater than or equal to 0.");

    this.args = args;
    decimalPlaces = decimalArgument.intValue();
  }

  @Override
  public String format(TsdlSample obj) {
    Conditions.checkNotNull(Condition.ARGUMENT, obj, "Sample to format must not be null.");

    var format = "0" + (decimalPlaces > 0 ? String.format(".%s", "0".repeat(decimalPlaces)) : "");
    var decimalFormat = new DecimalFormat(format);
    var symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setDecimalSeparator('.');
    decimalFormat.setDecimalFormatSymbols(symbols);
    decimalFormat.setGroupingUsed(false);

    var sampleName = obj.identifier().name();
    var aggregatorFunction = obj.aggregator().type().representation();
    var value = obj.aggregator().value();
    var lowerBound = obj.aggregator().lowerBound().orElse(null);
    var upperBound = obj.aggregator().upperBound().orElse(null);

    var formattedString = "sample %s(%s) with ID '%s' := %s".formatted(
        aggregatorFunction,
        lowerBound == null && upperBound == null
            ? ""
            : String.join(
                ", ",
                "\"" + (lowerBound != null ? lowerBound.toString() : "") + "\"",
                "\"" + (upperBound != null ? upperBound.toString() : "") + "\""
            ),
        sampleName,
        decimalFormat.format(value)
    );

    log.debug(formattedString);
    return formattedString;
  }

  @Override
  public String[] args() {
    return args;
  }
}
