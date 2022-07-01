package org.tsdl.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.infrastructure.dto.QueryResultDto;

@Slf4j
class CsvSerializingTsdlClientTest {
  private static final CsvSerializingTsdlClient csvClient = new CsvSerializingTsdlClient();

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsDataPoints_writesFileCorrectly")
  void query_serviceReturnsDataPoints_deserializesCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                            String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsPeriod_writesFileCorrectly")
  void query_serviceReturnsPeriod_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                      String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsPeriodSet_writesFileCorrectly")
  void query_serviceReturnsPeriodSet_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                         String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsScalar_writesFileCorrectly")
  void query_serviceReturnsScalar_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                      String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvSerializingTsdClientTestDataFactory#query_serviceReturnsScalarList_writesFileCorrectly")
  void query_serviceReturnsScalarList_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                          String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  private void executeTest(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse, String expectedContent) throws IOException {
    var filePath = Path.of(spec.targetFile());

    try {
      var result = csvClient.query(spec, serverResponse);

      var writtenFile = Files.readString(filePath);
      var normalizedExpected = expectedContent.replaceAll("(\\r\\n|\\r|\\n)", System.lineSeparator());
      assertThat(writtenFile).isEqualTo(normalizedExpected);

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
