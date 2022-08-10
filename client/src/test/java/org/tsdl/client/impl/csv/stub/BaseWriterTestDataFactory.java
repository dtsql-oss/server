package org.tsdl.client.impl.csv.stub;

import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.infrastructure.model.QueryResultType;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class BaseWriterTestDataFactory {
  private BaseWriterTestDataFactory() {
  }

  public static Stream<Arguments> writeDiscriminatorComment() {
    return Stream.of(
        Arguments.of("#TSDL Query Result\n#TYPE=PERIOD\n", tempPath(), QueryResultType.PERIOD),
        Arguments.of("#TSDL Query Result\n#TYPE=PERIOD_SET\n", tempPath(), QueryResultType.PERIOD_SET),
        Arguments.of("#TSDL Query Result\n#TYPE=SCALAR_LIST\n", tempPath(), QueryResultType.SCALAR_LIST),
        Arguments.of("#TSDL Query Result\n#TYPE=SCALAR\n", tempPath(), QueryResultType.SCALAR),
        Arguments.of("#TSDL Query Result\n#TYPE=DATA_POINTS\n", tempPath(), QueryResultType.DATA_POINTS)
    );
  }

  private static Path tempPath() {
    return Path.of(System.getProperty("java.io.tmpdir"), "BaseWriterTest_" + UUID.randomUUID() + ".csv");
  }
}
