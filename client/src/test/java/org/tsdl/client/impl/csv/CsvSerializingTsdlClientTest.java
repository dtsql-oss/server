package org.tsdl.client.impl.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.client.util.TsdlClientException;
import org.tsdl.infrastructure.dto.QueryDto;
import org.tsdl.infrastructure.dto.QueryResultDto;

@Slf4j
class CsvSerializingTsdlClientTest {
  private static final CsvSerializingTsdlClient csvClient = new CsvSerializingTsdlClient();

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsDataPoints_writesFileCorrectly")
  void query_serviceReturnsDataPoints_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse) {
    executeTest(spec, serverResponse);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsPeriod_writesFileCorrectly")
  void query_serviceReturnsPeriod_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse) {
    executeTest(spec, serverResponse);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsPeriodSet_writesFileCorrectly")
  void query_serviceReturnsPeriodSet_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse) {
    executeTest(spec, serverResponse);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsScalar_writesFileCorrectly")
  void query_serviceReturnsScalar_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse) {
    executeTest(spec, serverResponse);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsScalarList_writesFileCorrectly")
  void query_serviceReturnsScalarList_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse) {
    executeTest(spec, serverResponse);
  }

  @Test
  void query_incorrectClientSpecification_throws() {
    var incorrectSpec = new QueryClientSpecification() {
      @Override
      public QueryDto query() {
        return null;
      }

      @Override
      public String serverUrl() {
        return null;
      }
    };

    assertThatThrownBy(() -> csvClient.query(incorrectSpec))
        .isInstanceOf(TsdlClientException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class)
        .extracting(Throwable::getCause, InstanceOfAssertFactories.THROWABLE)
        .hasMessageStartingWith("Result type must be compatible with");
  }

  @Test
  void query_missingClientSpecification_throws() {
    assertThatThrownBy(() -> csvClient.query(null))
        .isInstanceOf(TsdlClientException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class);
  }

  private void executeTest(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse) {
    var filePath = Path.of(spec.targetFile());

    try {
      var result = csvClient.query(spec, serverResponse);
      assertThat(Files.exists(filePath)).isTrue(); // only check existence of file since CsvWriterTest already verifies correctness of contents
      assertThat(result).isInstanceOf(CsvSerializingTsdlClientResult.class);
    } finally {
      try {
        Files.delete(filePath);
      } catch (Exception e) {
        log.warn("Could not delete temp CSV file '{}': {}", spec.targetFile(), e.getMessage());
      }
    }
  }
}
