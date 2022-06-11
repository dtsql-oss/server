package org.tsdl.implementation.formatting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.factory.TsdlElementFactory;
import org.tsdl.implementation.model.common.TsdlFormattable;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.exception.TsdlParserException;
import org.tsdl.infrastructure.model.DataPoint;

class TsdlFormattingTests {
  private static final TsdlElementFactory ELEMENTS = ObjectFactory.INSTANCE.elementFactory();

  @Nested
  @DisplayName("Sample Formatting Tests")
  class SampleFormatting {
    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.formatting.stub.FormattingDataFactory#sampleArgs")
    void tsdlFormatter_withDecimalArguments(List<DataPoint> dps, AggregatorType type, String identifier, String[] args, String expectedResult)
        throws IOException {
      var sample = ELEMENTS.getSample(type, ELEMENTS.getIdentifier(identifier), true, args);
      sample.aggregator().compute(dps);
      formattingTest(sample, result -> assertThat(result).isEqualTo(expectedResult));
    }

    @Test
    void tsdlFormatter_missingArgument_throws() {
      var id = ELEMENTS.getIdentifier("id");
      assertThatThrownBy(() -> ELEMENTS.getSample(AggregatorType.AVERAGE, id, true))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tsdlFormatter_tooManyArguments_throws() {
      var id = ELEMENTS.getIdentifier("id");
      assertThatThrownBy(() -> ELEMENTS.getSample(AggregatorType.AVERAGE, id, true, "3", "4"))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tsdlFormatter_negativeArgument_throws() {
      var id = ELEMENTS.getIdentifier("id");
      assertThatThrownBy(() -> ELEMENTS.getSample(AggregatorType.AVERAGE, id, true, "-3"))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tsdlFormatter_invalidArgument_throws() {
      var id = ELEMENTS.getIdentifier("id");
      assertThatThrownBy(() -> ELEMENTS.getSample(AggregatorType.AVERAGE, id, true, "3.4"))
          .isInstanceOf(TsdlParserException.class);
    }
  }

  <T extends TsdlFormattable<T>> void formattingTest(TsdlFormattable<T> formattable, Consumer<String> testBody) throws IOException {
    var stream = new ListeningOutputStream();
    formattable.echo(stream);
    testBody.accept(stream.getData());
  }
}
