package org.tsdl.testutil.visualization.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures the visualization of test case data prior to execution.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TsdlTestVisualization {
  String PRECISE_AXIS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
  String DEFAULT_AXIS_FORMAT = "";

  /**
   * If true, test data visualization is skipped.
   */
  boolean skipVisualization() default false;

  /**
   * If true, each data point is clearly indicated with a shape. Otherwise, data points are only interpolated using straight lines.
   */
  boolean renderPointShape() default true;

  /**
   * Specifies the date-time format used to display date-times on the primary axis.
   */
  String dateAxisFormat() default DEFAULT_AXIS_FORMAT;
}
