package org.tsdl.client.impl.csv.writer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.client.impl.csv.CsvSerializingQueryClientSpecification;
import org.tsdl.client.util.QueryResultWriterFactory;
import org.tsdl.infrastructure.dto.QueryResultDto;

@Slf4j
class CsvWriterTest {
  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvWriterTestDataFactory#query_writeDataPoints_writesFileCorrectly")
  void query_writeDataPoints_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                          String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvWriterTestDataFactory#query_writePeriod_writesFileCorrectly")
  void query_writePeriod_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                      String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvWriterTestDataFactory#query_writePeriodSet_writesFileCorrectly")
  void query_writePeriodSet_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                         String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvWriterTestDataFactory#query_writeScalar_writesFileCorrectly")
  void query_writeScalar_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                      String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvWriterTestDataFactory#query_writeScalarList_writesFileCorrectly")
  void query_writeScalarList_writesFileCorrectly(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse,
                                                          String expectedContents) throws IOException {
    executeTest(spec, serverResponse, expectedContents);
  }

  private void executeTest(CsvSerializingQueryClientSpecification spec, QueryResultDto serverResponse, String expectedContent) throws IOException {
    var filePath = Path.of(spec.targetFile());

    try {
      var writer = QueryResultWriterFactory.getCsvWriter(serverResponse.getType());
      writer.write(serverResponse.getResult(), spec);

      var writtenFile = Files.readString(filePath);
      var normalizedExpected = expectedContent.replaceAll("(\\r\\n|\\r|\\n)", System.lineSeparator());
      assertThat(writtenFile).isEqualTo(normalizedExpected);
    } finally {
      try {
        Files.delete(filePath);
      } catch (Exception e) {
        log.warn("Could not delete temp CSV file '{}': {}", spec.targetFile(), e.getMessage());
      }
    }
  }
}
