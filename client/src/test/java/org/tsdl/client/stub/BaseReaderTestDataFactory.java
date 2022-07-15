package org.tsdl.client.stub;

import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.infrastructure.model.QueryResultType;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class BaseReaderTestDataFactory {
  private BaseReaderTestDataFactory() {
  }

  public static Stream<Arguments> peekType() {
    return Stream.of(
        Arguments.of("#TSDL Query Result\n#TYPE=PERIOD", tempPath(), QueryResultType.PERIOD),
        Arguments.of("#TSDL Query Result\n#TYPE=PERIOD_SET", tempPath(), QueryResultType.PERIOD_SET),
        Arguments.of("#TSDL Query Result\n#TYPE=SCALAR_LIST", tempPath(), QueryResultType.SCALAR_LIST),
        Arguments.of("#TSDL Query Result\n#TYPE=SCALAR", tempPath(), QueryResultType.SCALAR),
        Arguments.of("#TSDL Query Result\n#TYPE=DATA_POINTS", tempPath(), QueryResultType.DATA_POINTS)
    );
  }

  private static Path tempPath() {
    return Path.of(System.getProperty("java.io.tmpdir"), "BaseReaderTest_" + UUID.randomUUID() + ".csv");
  }
}
