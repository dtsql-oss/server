package org.tsdl.client.impl.csv.writer;

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
class BaseWriterTest {
  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.csv.stub.BaseWriterTestDataFactory#writeDiscriminatorComment")
  void writeDiscriminatorComment(String expectedContent, Path filePath, QueryResultType type) throws IOException {
    // closing before asserting causes flush which is necessary to assert successfully (otherwise, file would be empty)
    try (var writer = BaseWriter.createWriter(filePath.toString())) {
      BaseWriter.writeDiscriminatorComment(writer, type);
    } finally {
      var writtenContent = Files.readString(filePath, StandardCharsets.UTF_8);
      assertThat(writtenContent).isEqualTo(expectedContent.replaceAll("(\\r\\n|\\r|\\n)", System.lineSeparator()));

      try {
        Files.delete(filePath);
      } catch (Exception e) {
        log.warn("Could not delete temp CSV file '{}': {}", filePath, e.getMessage());
      }
    }
  }
}
