package org.tsdl.client.impl.csv.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.client.util.QueryResultReaderFactory;
import org.tsdl.infrastructure.model.QueryResult;

@Slf4j
class CsvReaderTest {
  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvReaderTestDataFactory#query_writeDataPoints_writesFileCorrectly")
  void query_writeDataPoints_writesFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvReaderTestDataFactory#query_writePeriod_writesFileCorrectly")
  void query_writePeriod_writesFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvReaderTestDataFactory#query_writePeriodSet_writesFileCorrectly")
  void query_writePeriodSet_writesFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvReaderTestDataFactory#query_writeScalar_writesFileCorrectly")
  void query_writeScalar_writesFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.CsvReaderTestDataFactory#query_writeScalarList_writesFileCorrectly")
  void query_writeScalarList_writesFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  private void executeTest(String serializedContent, Path serializedPath, QueryResult expectedResult) throws IOException {
    try {
      Files.writeString(serializedPath, serializedContent, StandardCharsets.UTF_8);
      var reader = QueryResultReaderFactory.getCsvReader(expectedResult.type());
      var deserializedResult = reader.read(serializedPath.toString());

      assertThat(deserializedResult)
          .usingRecursiveComparison()
          .isEqualTo(expectedResult);
    } finally {
      try {
        Files.delete(serializedPath);
      } catch (Exception e) {
        log.warn("Could not delete temp CSV file '{}': {}", serializedPath, e.getMessage());
      }
    }
  }
}
