package org.tsdl.client.impl.builder.stub;

import static org.tsdl.client.api.builder.FilterSpecification.not;
import static org.tsdl.client.api.builder.Range.IntervalType.CLOSED;
import static org.tsdl.client.api.builder.Range.IntervalType.OPEN;
import static org.tsdl.client.api.builder.Range.IntervalType.OPEN_END;
import static org.tsdl.client.api.builder.Range.IntervalType.OPEN_START;
import static org.tsdl.client.api.builder.TsdlQueryBuilder.as;
import static org.tsdl.client.impl.builder.ChoiceSpecificationImpl.follows;
import static org.tsdl.client.impl.builder.ChoiceSpecificationImpl.precedes;
import static org.tsdl.client.impl.builder.EventSpecificationImpl.event;
import static org.tsdl.client.impl.builder.FilterConnectiveSpecificationImpl.and;
import static org.tsdl.client.impl.builder.FilterConnectiveSpecificationImpl.or;
import static org.tsdl.client.impl.builder.QueryPeriodImpl.period;
import static org.tsdl.client.impl.builder.RangeImpl.for_;
import static org.tsdl.client.impl.builder.RangeImpl.within;
import static org.tsdl.client.impl.builder.TemporalFilterSpecificationImpl.after;
import static org.tsdl.client.impl.builder.TemporalFilterSpecificationImpl.before;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.averageTemporal;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.countTemporal;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.maximumTemporal;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.minimumTemporal;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.standardDeviationTemporal;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.sumTemporal;
import static org.tsdl.client.impl.builder.ThresholdFilterSpecificationImpl.gt;
import static org.tsdl.client.impl.builder.ThresholdFilterSpecificationImpl.lt;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.average;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.count;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.integral;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.maximum;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.minimum;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.standardDeviation;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.sum;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.allPeriods;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.dataPoints;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.longestPeriod;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.sample;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.samples;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.shortestPeriod;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.client.api.builder.EventSpecification;
import org.tsdl.client.api.builder.TemporalSampleSpecification;
import org.tsdl.client.api.builder.ValueSampleSpecification;
import org.tsdl.client.impl.builder.DeviationFilterSpecificationImpl;
import org.tsdl.infrastructure.common.TsdlTimeUnit;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class TsdlQueryBuilderTestDataFactory {
  private TsdlQueryBuilderTestDataFactory() {
  }

  public static Stream<Arguments> temporalSampleInput() {
    return Stream.of(
        Arguments.of(
            averageTemporal("s1", TsdlTimeUnit.MINUTES, period("2022-04-03T12:45:03.123Z", "2022-04-05T12:45:03.123Z")),
            "avg_t(minutes, \"2022-04-03T12:45:03.123Z/2022-04-05T12:45:03.123Z\") AS s1"
        ),
        Arguments.of(
            sumTemporal("s2", TsdlTimeUnit.HOURS, period("2022-04-03T12:45:03.123Z", "2022-04-05T12:45:03.123Z"),
                period("2022-04-06T12:45:03.123+03:00", "2022-04-07T12:45:03.123-08:30")),
            "sum_t(hours, \"2022-04-03T12:45:03.123Z/2022-04-05T12:45:03.123Z\", \"2022-04-06T09:45:03.123Z/2022-04-07T21:15:03.123Z\") AS s2"
        ),
        Arguments.of(
            maximumTemporal("s3", TsdlTimeUnit.WEEKS, period("2022-04-03T12:45:03.123Z", "2022-04-05T12:45:03.123Z"),
                period("2022-04-06T12:45:03.123+03:00", "2022-04-07T12:45:03.123-08:30")),
            "max_t(weeks, \"2022-04-03T12:45:03.123Z/2022-04-05T12:45:03.123Z\", \"2022-04-06T09:45:03.123Z/2022-04-07T21:15:03.123Z\") AS s3"
        ),
        Arguments.of(
            minimumTemporal("s4", TsdlTimeUnit.SECONDS, period("2022-04-03T12:45:03.123Z", "2022-04-05T12:45:03.123Z"),
                period("2022-04-06T12:45:03.123+03:00", "2022-04-07T12:45:03.123-08:30")),
            "min_t(seconds, \"2022-04-03T12:45:03.123Z/2022-04-05T12:45:03.123Z\", \"2022-04-06T09:45:03.123Z/2022-04-07T21:15:03.123Z\") AS s4"
        ),
        Arguments.of(
            standardDeviationTemporal("s5", TsdlTimeUnit.MILLISECONDS, period("2022-04-03T12:45:03.123Z", "2022-04-05T12:45:03.123Z"),
                period("2022-04-06T12:45:03.123+03:00", "2022-04-07T12:45:03.123-08:30")),
            "stddev_t(millis, \"2022-04-03T12:45:03.123Z/2022-04-05T12:45:03.123Z\", \"2022-04-06T09:45:03.123Z/2022-04-07T21:15:03.123Z\") AS s5"
        ),
        Arguments.of(
            countTemporal("s6", period("2022-04-03T12:45:03.123Z", "2022-04-05T12:45:03.123Z"),
                period("2022-04-06T12:45:03.123+03:00", "2022-04-07T12:45:03.123-08:30")),
            "count_t(\"2022-04-03T12:45:03.123Z/2022-04-05T12:45:03.123Z\", \"2022-04-06T09:45:03.123Z/2022-04-07T21:15:03.123Z\") AS s6"
        )
    );
  }

  public static Stream<Arguments> temporalSamplesInput() {
    return TsdlQueryBuilderTestDataFactory.<TemporalSampleSpecification>wrapArguments(temporalSampleInput());
  }

  public static Stream<Arguments> valueSampleInput() {
    return Stream.of(
        Arguments.of(
            average("s1", null, (String) null),
            "avg() AS s1"
        ),
        Arguments.of(
            sum("s1", "2022-04-03T12:45:03.123Z", null),
            "sum(\"2022-04-03T12:45:03.123Z\", \"\") AS s1"
        ),
        Arguments.of(
            minimum("s1", null, Instant.parse("2022-04-03T12:45:03.123Z")),
            "min(\"\", \"2022-04-03T12:45:03.123Z\") AS s1"
        ),
        Arguments.of(
            maximum("s1", Instant.parse("2022-04-03T11:45:03.123Z"), Instant.parse("2022-04-03T12:45:03.123Z")),
            "max(\"2022-04-03T11:45:03.123Z\", \"2022-04-03T12:45:03.123Z\") AS s1"
        ),
        Arguments.of(
            standardDeviation("s1", "2022-04-03T11:45:03.123Z", "2022-04-03T12:45:03.123Z"),
            "stddev(\"2022-04-03T11:45:03.123Z\", \"2022-04-03T12:45:03.123Z\") AS s1"
        ),
        Arguments.of(
            count("s1", null, Instant.parse("2022-04-03T12:45:03.123Z")),
            "count(\"\", \"2022-04-03T12:45:03.123Z\") AS s1"
        ),
        Arguments.of(
            integral("s1", (Instant) null, null),
            "integral() AS s1"
        )
    );
  }

  public static Stream<Arguments> valueSamplesInput() {
    return TsdlQueryBuilderTestDataFactory.<ValueSampleSpecification>wrapArguments(valueSampleInput());
  }

  public static Stream<Arguments> filterInput() {
    return Stream.of(
        Arguments.of(
            and(
                gt("s2"),
                not(lt(3.5))
            ),
            "AND(gt(s2), NOT(lt(3.5)))"
        ),
        Arguments.of(
            or(
                not(before("2022-07-03T12:45:03.123Z")),
                after(Instant.parse("2022-07-03T12:45:03.123Z"))
            ),
            "OR(NOT(before(\"2022-07-03T12:45:03.123Z\")), after(\"2022-07-03T12:45:03.123Z\"))"
        ),
        Arguments.of(
            and(
                DeviationFilterSpecificationImpl.aroundRelative(20.0, 34.0, true),
                DeviationFilterSpecificationImpl.aroundAbsolute("s1", "s7")
            ),
            "AND(NOT(around(rel, 20.0, 34.0)), around(abs, s1, s7))"
        )
    );
  }

  public static Stream<Arguments> eventInput() {
    return Stream.of(
        Arguments.of(
            event(and(lt(3.5)), for_(3L, OPEN_START, TsdlTimeUnit.WEEKS), as("low")),
            "AND(lt(3.5)) FOR (3,] weeks AS low"
        ),
        Arguments.of(
            event(or(not(gt(7.0))), for_(3L, OPEN, TsdlTimeUnit.SECONDS), as("high")),
            "OR(NOT(gt(7.0))) FOR (3,) seconds AS high"
        ),
        Arguments.of(
            event(and(gt("s2")), as("mid")),
            "AND(gt(s2)) AS mid"
        )
    );
  }

  public static Stream<Arguments> eventsInput() {
    return TsdlQueryBuilderTestDataFactory.<EventSpecification>wrapArguments(eventInput());
  }

  public static Stream<Arguments> choiceInput() {
    return Stream.of(
        Arguments.of(
            precedes("low", "high", within(23L, 26L, TsdlTimeUnit.MINUTES, OPEN_START)),
            "low precedes high WITHIN (23,26] minutes"
        ),
        Arguments.of(
            follows("low", "high", within(23L, TsdlTimeUnit.DAYS, OPEN_END)),
            "low follows high WITHIN [,23) days"
        ),
        Arguments.of(
            follows("low", "high", within(26L, CLOSED, TsdlTimeUnit.MILLISECONDS)),
            "low follows high WITHIN [26,] millis"
        )
    );
  }

  public static Stream<Arguments> yieldInput() {
    return Stream.of(
        Arguments.of(dataPoints(), "data points"),
        Arguments.of(allPeriods(), "all periods"),
        Arguments.of(longestPeriod(), "longest period"),
        Arguments.of(shortestPeriod(), "shortest period"),
        Arguments.of(sample("s1"), "sample s1"),
        Arguments.of(samples("s1"), "samples s1"),
        Arguments.of(samples("s1", "s2", "s3"), "samples s1, s2, s3")
    );
  }

  @SuppressWarnings("unchecked")
  private static <T> Stream<Arguments> wrapArguments(Stream<Arguments> argumentsStream) {
    var specs = new ArrayList<T>();
    var expectedStrings = new ArrayList<String>();
    argumentsStream.forEach(i -> {
      var args = i.get();
      specs.add((T) args[0]);
      expectedStrings.add((String) args[1]);
    });
    return Stream.of(Arguments.of(specs, String.join(",\n  ", expectedStrings)));

  }
}
