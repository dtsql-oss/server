package org.tsdl.testutil.creation.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a resource file with data about a time series to be used as input argument for a unit test.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TsdlTestSource {
  /**
   * The path to the resource file.
   */
  String value();

  /**
   * The timestamp format to be used when parsing date-time information from the resource file.
   */
  String timestampFormat() default "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * The number of rows to skip in the resource file before the actual data starts.
   */
  int skipHeaders() default 0;
}
