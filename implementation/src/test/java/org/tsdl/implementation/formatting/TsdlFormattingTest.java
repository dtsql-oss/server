package org.tsdl.implementation.formatting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.model.common.TsdlFormattable;
import org.tsdl.implementation.model.sample.aggregation.SummaryAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.exception.TsdlParseException;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlLogEvent;

class TsdlFormattingTest {
  private static final TsdlQueryElementFactory ELEMENTS = TsdlComponentFactory.INSTANCE.elementFactory();
  private static final TsdlComponentFactory COMPONENTS = TsdlComponentFactory.INSTANCE;

  @Nested
  @DisplayName("Sample Formatting Tests")
  class SampleFormatting {
    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.formatting.stub.FormattingDataFactory#sampleArgs")
    void tsdlFormatter_outputStreamWithDecimalArguments(List<DataPoint> dps, AggregatorType type, Instant lowerBound, Instant upperBound,
                                                        String identifier, String[] args, String expectedResult)
        throws IOException {
      var sample = ELEMENTS.getSample(type, lowerBound, upperBound, ELEMENTS.getIdentifier(identifier), true, args);
      if (sample.aggregator() instanceof SummaryAggregator summaryAggregator) {
        summaryAggregator.setStatistics(COMPONENTS.summaryStatistics());
      }

      sample.aggregator().compute(identifier, dps);
      formattingTestStream(sample, result -> assertThat(result).isEqualTo(expectedResult + "\n"));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.formatting.stub.FormattingDataFactory#sampleArgs")
    void tsdlFormatter_collectionTargetWithDecimalArguments(List<DataPoint> dps, AggregatorType type, Instant lowerBound, Instant upperBound,
                                                            String identifier, String[] args, String expectedResult) {
      var sample = ELEMENTS.getSample(type, lowerBound, upperBound, ELEMENTS.getIdentifier(identifier), true, args);
      if (sample.aggregator() instanceof SummaryAggregator summaryAggregator) {
        summaryAggregator.setStatistics(COMPONENTS.summaryStatistics());
      }

      sample.aggregator().compute(identifier, dps);
      formattingTestCollection(sample, result -> assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.list(TsdlLogEvent.class))
          .hasSize(1)
          .satisfies(e -> assertThat(e.get(0).message()).isEqualTo(expectedResult))
      );
    }

    @Test
    void tsdlFormatter_missingArgument_throws() {
      var id = ELEMENTS.getIdentifier("id");
      assertThatThrownBy(() -> ELEMENTS.getSample(AggregatorType.AVERAGE, null, null, id, true))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tsdlFormatter_tooManyArguments_throws() {
      var id = ELEMENTS.getIdentifier("id");
      assertThatThrownBy(() -> ELEMENTS.getSample(AggregatorType.AVERAGE, null, null, id, true, "3", "4"))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tsdlFormatter_negativeArgument_throws() {
      var id = ELEMENTS.getIdentifier("id");
      assertThatThrownBy(() -> ELEMENTS.getSample(AggregatorType.AVERAGE, null, null, id, true, "-3"))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tsdlFormatter_invalidArgument_throws() {
      var id = ELEMENTS.getIdentifier("id");
      assertThatThrownBy(() -> ELEMENTS.getSample(AggregatorType.AVERAGE, null, null, id, true, "3.4"))
          .isInstanceOf(TsdlParseException.class);
    }
  }

  <T extends TsdlFormattable<T>> void formattingTestStream(TsdlFormattable<T> formattable, Consumer<String> testBody) throws IOException {
    var stream = new ListeningOutputStream();
    formattable.echo(stream);
    testBody.accept(stream.getData());
  }

  <T extends TsdlFormattable<T>> void formattingTestCollection(TsdlFormattable<T> formattable, Consumer<List<TsdlLogEvent>> testBody) {
    var lst = new ArrayList<TsdlLogEvent>();
    formattable.echo(lst);
    testBody.accept(lst);
  }
}
