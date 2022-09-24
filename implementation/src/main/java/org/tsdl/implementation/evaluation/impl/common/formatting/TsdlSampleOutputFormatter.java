package org.tsdl.implementation.evaluation.impl.common.formatting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.model.common.TsdlOutputFormatter;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalAggregatorWithUnit;
import org.tsdl.implementation.model.sample.aggregation.value.ValueAggregator;
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
    decimalPlaces = (int) decimalArgument;
  }

  @Override
  public String format(TsdlSample obj) {
    Conditions.checkNotNull(Condition.ARGUMENT, obj, "Sample to format must not be null.");

    var sampleName = obj.identifier().name();
    var aggregatorFunction = obj.aggregator().type().representation();
    var formattedValue = getDecimalFormat().format(obj.aggregator().value());

    var descriptor = switch (obj.aggregator()) {
      case ValueAggregator v -> getSampleDescriptor(v, aggregatorFunction);
      case TemporalAggregator t -> getSampleDescriptor(t, aggregatorFunction);
      default -> throw Conditions.exception(Condition.STATE, "Cannot format sample '%s', unrecognized aggregator type.", sampleName);
    };

    var suffix = switch (obj.aggregator()) {
      case ValueAggregator ignored -> "";
      case TemporalAggregatorWithUnit tu -> tu.unit().representation();
      case TemporalAggregator ignored -> /* noinspection DuplicateBranchesInSwitch */ ""; // no unit -> no suffix
      default -> throw Conditions.exception(Condition.STATE, "Cannot format sample '%s', unrecognized aggregator type.", sampleName);
    };

    var formattedString = "sample '%s' %s := %s%s".formatted(
        sampleName,
        descriptor,
        formattedValue,
        suffix != null && !suffix.isEmpty() ? " " + suffix : ""
    );

    log.debug(formattedString);
    return formattedString;
  }

  private String getSampleDescriptor(ValueAggregator valueAggregator, String aggregatorFunction) {
    var lowerBound = valueAggregator.lowerBound().orElse(null);
    var upperBound = valueAggregator.upperBound().orElse(null);
    var localSampleArguments = String.join(
        ", ",
        "\"" + (lowerBound != null ? lowerBound.toString() : "") + "\"",
        "\"" + (upperBound != null ? upperBound.toString() : "") + "\""
    );

    return "%s(%s)".formatted(
        aggregatorFunction,
        lowerBound == null && upperBound == null ? "" : localSampleArguments
    );
  }

  private String getSampleDescriptor(TemporalAggregator temporalAggregator, String aggregatorFunction) {
    var unit = temporalAggregator instanceof TemporalAggregatorWithUnit t ? t.unit().representation() + ", " : "";
    var temporalSampleArguments = temporalAggregator.periods().stream()
        .map(p -> "\"%s/%s\"".formatted(p.start(), p.end()))
        .collect(Collectors.joining(", "));

    return "%s(%s%s)".formatted(aggregatorFunction, unit, temporalSampleArguments);
  }

  private NumberFormat getDecimalFormat() {
    var format = "0" + (decimalPlaces > 0 ? String.format(".%s", "0".repeat(decimalPlaces)) : "");
    var decimalFormat = new DecimalFormat(format);
    var symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setDecimalSeparator('.');
    decimalFormat.setDecimalFormatSymbols(symbols);
    decimalFormat.setGroupingUsed(false);
    return decimalFormat;
  }

  @Override
  public String[] args() {
    return args;
  }
}
