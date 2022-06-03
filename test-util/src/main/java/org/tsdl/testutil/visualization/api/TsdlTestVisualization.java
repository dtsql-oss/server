package org.tsdl.testutil.visualization.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TsdlTestVisualization {
    String PRECISE_AXIS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    String DEFAULT_AXIS_FORMAT = "";

    boolean skipVisualization() default false;

    boolean renderPointShape() default true;

    String dateAxisFormat() default DEFAULT_AXIS_FORMAT;
}
