package org.tsdl.testutil.creation.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * A container for multiple {@link TsdlTestSource} annotations specifying resource files representing time series files to be used as
 * unit test input data.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(TsdlTestProvider.class)
public @interface TsdlTestSources {
  /**
   * Instances of {@link TsdlTestSource} specifying test resources, each defining a test source argument to be provided to the unit test.
   */
  TsdlTestSource[] value();
}
