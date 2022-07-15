package org.tsdl.client.impl.csv.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.infrastructure.model.QueryResultType;

@Slf4j
class BaseReaderTest {
  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.BaseReaderTestDataFactory#peekType")
  void peekType(String fileContent, Path filePath, QueryResultType expectedType) throws IOException {
    try {
      Files.writeString(filePath, fileContent, StandardCharsets.UTF_8);
      var peekedType = BaseReader.peekType(filePath.toString());

      assertThat(peekedType).isEqualTo(expectedType);
    } finally {
      try {
        Files.delete(filePath);
      } catch (Exception e) {
        log.warn("Could not delete temp CSV file '{}': {}", filePath, e.getMessage());
      }
    }
  }
}
