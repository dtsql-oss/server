package org.tsdl.testutil.creation.provider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

public class TsdlTestProvider implements ArgumentsProvider, AnnotationConsumer<TsdlTestSources> {
  private List<TsdlTestSource> sourceFiles;

  @Override
  public void accept(TsdlTestSources tsdlTestSources) {
    Conditions.checkNotNull(Condition.ARGUMENT, tsdlTestSources, "%s argument must not be null.", TsdlTestSources.class.getSimpleName());
    Conditions.checkNotNull(Condition.ARGUMENT, tsdlTestSources.value(), "%s's value must not be null.", TsdlTestSources.class.getSimpleName());

    sourceFiles = new ArrayList<>();
    for (TsdlTestSource source : tsdlTestSources.value()) {
      Conditions.checkNotNull(Condition.ARGUMENT, source.value(), "%s's value must not be null.", TsdlTestSource.class.getSimpleName());
      sourceFiles.add(source);
    }
  }

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
    Conditions.checkNotNull(Condition.STATE, sourceFiles, "List of test source files has not been initialized.");

    var arguments = new Object[sourceFiles.size()];
    for (int i = 0; i < sourceFiles.size(); i++) {
      var testSource = sourceFiles.get(i);
      var testUrl = Thread.currentThread().getContextClassLoader().getResource(testSource.value());
      Conditions.checkNotNull(Condition.STATE, testUrl, "Could not read file '%s'", testSource.value());

      //noinspection ConstantConditions
      var testFile = testUrl.getPath();
      var dataPoints = new ArrayList<DataPoint>();
      var formatter = DateTimeFormatter
          .ofPattern(testSource.timestampFormat())
          .withZone(ZoneOffset.UTC);

      try (var reader = new BufferedReader(new FileReader(testFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
          var split = line.split(";");
          var date = split[0].trim();
          var value = split[1].trim();

          dataPoints.add(DataPoint.of(
              formatter.parse(date, Instant::from),
              Double.parseDouble(value))
          );
        }
      }

      arguments[i] = dataPoints;
    }

    return Stream.of(
        Arguments.of(arguments)
    );
  }
}
