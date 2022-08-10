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
  @MethodSource("org.tsdl.client.impl.csv.stub.CsvReaderTestDataFactory#query_readDataPoints_readsFileCorrectly")
  void query_readDataPoints_readsFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.csv.stub.CsvReaderTestDataFactory#query_readPeriod_readsFileCorrectly")
  void query_readPeriod_readsFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.csv.stub.CsvReaderTestDataFactory#query_readPeriodSet_readsFileCorrectly")
  void query_readPeriodSet_readsFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.csv.stub.CsvReaderTestDataFactory#query_readScalar_readsFileCorrectly")
  void query_readScalar_readsFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
    executeTest(fileContent, tempPath, expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.csv.stub.CsvReaderTestDataFactory#query_readScalarList_readsFileCorrectly")
  void query_readScalarList_readsFileCorrectly(Path tempPath, QueryResult expectedResult, String fileContent) throws IOException {
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
