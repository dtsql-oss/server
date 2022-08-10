package org.tsdl.client.impl.builder;

import static org.tsdl.client.api.builder.FilterSpecification.not;
import static org.tsdl.client.api.builder.Range.IntervalType.OPEN_START;
import static org.tsdl.client.api.builder.TsdlQueryBuilder.as;
import static org.tsdl.client.impl.builder.ChoiceSpecificationImpl.precedes;
import static org.tsdl.client.impl.builder.EventSpecificationImpl.event;
import static org.tsdl.client.impl.builder.FilterConnectiveSpecificationImpl.and;
import static org.tsdl.client.impl.builder.FilterConnectiveSpecificationImpl.or;
import static org.tsdl.client.impl.builder.QueryPeriodImpl.period;
import static org.tsdl.client.impl.builder.RangeImpl.within;
import static org.tsdl.client.impl.builder.TemporalFilterSpecificationImpl.after;
import static org.tsdl.client.impl.builder.TemporalFilterSpecificationImpl.before;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.countTemporal;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.maximumTemporal;
import static org.tsdl.client.impl.builder.ThresholdFilterSpecificationImpl.gt;
import static org.tsdl.client.impl.builder.ThresholdFilterSpecificationImpl.lt;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.average;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.integral;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.standardDeviation;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.sum;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.allPeriods;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.samples;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.tsdl.client.api.builder.TsdlQueryBuilder;
import org.tsdl.infrastructure.common.TsdlTimeUnit;

class TsdlQueryBuilderTest {
  @Test
  void test() {
    System.out.println(
        TsdlQueryBuilder.instance()
            .temporalSamples(
                maximumTemporal(
                    "s1",
                    TsdlTimeUnit.MILLISECONDS,
                    period("2022-07-08T12:30:14.123+03:00", "2022-07-09T12:30:14.123+03:00"),
                    period("2022-07-10T12:30:14.123+03:00", "2022-07-11T12:30:14.123+03:00")
                ),
                countTemporal(
                    "s2",
                    period("2022-07-08T12:30:14.123+03:00", "2022-07-09T12:30:14.123+03:00"),
                    period("2022-07-10T12:30:14.123+03:00", "2022-07-11T12:30:14.123+03:00")
                )
            )
            .valueSamples(
                average("s3", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS)),
                integral("s4", null, Instant.now().plus(1, ChronoUnit.DAYS)),
                standardDeviation("s5", Instant.now(), null),
                sum("s6", null, null)
            )
            .filter(
                and(
                    gt("s2"),
                    not(lt(3.5)),
                    not(before("2022-07-03T12:45:03.123Z")),
                    after(Instant.parse("2022-07-03T12:45:03.123Z")),
                    DeviationFilterSpecificationImpl.aroundRelative(20.0, 34.0, true),
                    DeviationFilterSpecificationImpl.aroundAbsolute("s1", "s7")
                )
            )
            .events(
                event(and(lt(3.5)), RangeImpl.for_(3L, OPEN_START, TsdlTimeUnit.WEEKS), as("low")),
                event(or(not(gt(7.0))), as("high")),
                event(and(gt("s2")), as("mid"))
            )
            .choice(precedes("low", "high", within(23L, 26L, TsdlTimeUnit.MINUTES, OPEN_START)))
            .yield(samples("s1", "s2", "s3"))
            .yield(allPeriods())
            .build()
    );
  }
}
