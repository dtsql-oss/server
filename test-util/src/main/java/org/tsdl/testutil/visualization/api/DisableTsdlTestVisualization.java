package org.tsdl.testutil.visualization.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Disables the visualization using a {@link TimeSeriesTestVisualizer} of all tests in a test class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableTsdlTestVisualization {
}
